import { repository, Count } from '@loopback/repository';
import { service } from '@loopback/core';
import { AccountRepository, UserRepository, StoreOwnerRepository } from "../repositories"; // ADDED STORE OWNER REPOSITORY
import { AuthService } from "../services/auth.service";
import { patch, requestBody, param, HttpErrors, get, response } from '@loopback/rest';
import { authenticate } from '@loopback/authentication';
import { SecurityBindings, securityId, UserProfile } from '@loopback/security';
import { inject } from '@loopback/core';

export type UpdateFcmTokenBody = {
    fcmToken: string;
};

export class AccountController {
    constructor(
        @repository(AccountRepository)
        public accountRepository: AccountRepository,
        @repository(UserRepository)
        public userRepository: UserRepository,
        @repository(StoreOwnerRepository)
        public storeOwnerRepository: StoreOwnerRepository, // ADDED STORE OWNER REPOSITORY
        @service(AuthService)
        public authService: AuthService,
    ) {}

    // ========== AUTHENTICATED ENDPOINTS (MUST BE BEFORE /account) ==========

    @authenticate('jwt')
    @get('/account/me')
    @response(200, {
        description: 'Get current user',
        content: {
            'application/json': {
                schema: {
                    type: 'object',
                    properties: {
                        id: {type: 'number'},
                        fcm_token: {type: 'string'},
                        notifications: {type: 'boolean'},
                        picture: {type: 'string'},
                        account_email: {type: 'string'},
                        full_name: {type: 'string'},
                        role: {type: 'string'}, // Added to verify if user is a store owner or not
                    },
                },
            },
        },
    })
    async getCurrentUser(
        @inject(SecurityBindings.USER) currentUserProfile: UserProfile,
    ): Promise<any> {
        try {
            const userEmail = currentUserProfile[securityId];
            if (!userEmail) {
                throw new HttpErrors.Unauthorized('User not authenticated');
            }
            
            console.log('Getting user for email:', userEmail);
            
            const user = await this.userRepository.findOne({
                where: {account_email: userEmail as string}
            });
            
            if (!user) {
                console.error('User not found for email:', userEmail);
                throw new HttpErrors.NotFound('User not found');
            }
            
            console.log('User found:', user.id);
            
            // Get account to include full_name AND ROLE!
            let fullName = '';
            let role = '';
            let storeInfo = null;
            try {
                const account = await this.accountRepository.findOne({
                    where: {email: userEmail as string}
                });
                fullName = account?.full_name || '';
                role = account?.role || 'customer';  // ADDED THIS LINE TO GET THE ROLE OF THE USER

                // IF IT'S ASTORE OWNER, GET STORE INFO
            if (role === 'store_owner') {
                const storeOwner = await this.storeOwnerRepository.findOne({
                    where: {account_email: userEmail as string}
                });
                if (storeOwner) {
                    storeInfo = {
                        country: storeOwner.country,
                        city: storeOwner.city,
                        postal_code: storeOwner.postal_code,
                        street_name: storeOwner.street_name,
                        street_number: storeOwner.street_number
                    };
                }
            }

                console.log('Account found, full_name:', fullName, 'role:', role); //UPDATED THIS LINE TO PRINT THE ROLE OF THE USER
            } catch (accountError: any) {
                console.error('Error getting account:', accountError.message);
                // Don't fail if account lookup fails, just use empty string
                fullName = '';
                role = 'customer'; // ADDED THIS LINE 
            }
            
            const result = {
                id: user.id,
                fcm_token: user.fcm_token,
                notifications: user.notifications,
                picture: user.picture || null,
                account_email: user.account_email,
                full_name: fullName,
                role: role, // ADDED THIS LINE TO RETURN THE ROLE OF THE USER
                store_info: storeInfo // ADDED THIS LINE TO RETURN THE STORE INFO
            };
            
            console.log('Returning user data:', result);
            return result;
        } catch (error: any) {
            console.error('=== GET /account/me ERROR ===');
            console.error('Error type:', error.constructor.name);
            console.error('Error message:', error.message);
            console.error('Error code:', error.code);
            console.error('Stack trace:', error.stack);
            console.error('===========================');
            
            if (error instanceof HttpErrors.HttpError) {
                throw error;
            }
            throw new HttpErrors[500](`Failed to get user: ${error.message || 'Unknown error'}`);
        }
    }

    @authenticate('jwt')
    @patch('/account/me')
    @response(204, {
        description: 'Update current user',
    })
    async updateCurrentUser(
        @inject(SecurityBindings.USER) currentUserProfile: UserProfile,
        @requestBody({
            content: {
                'application/json': {
                    schema: {
                        type: 'object',
                        properties: {
                            picture: {type: 'string'},
                            notifications: {type: 'boolean'},
                        },
                    },
                },
            },
        })
        updates: {
            picture?: string;
            notifications?: boolean;
        },
    ): Promise<void> {
        const userEmail = currentUserProfile[securityId];
        if (!userEmail) {
            throw new HttpErrors.Unauthorized('User not authenticated');
        }

        console.log('=== UPDATE /account/me ===');
        console.log('User email:', userEmail);
        console.log('Updates received:', {
            hasPicture: !!updates.picture,
            pictureLength: updates.picture?.length || 0,
            notifications: updates.notifications
        });

        try {
            const result = await this.userRepository.updateAll(
                updates,
                {account_email: userEmail as string}
            );
            
            console.log('Update result count:', result.count);
            
            // Verify the update by fetching the user
            const updatedUser = await this.userRepository.findOne({
                where: {account_email: userEmail as string}
            });
            
            console.log('Updated user picture exists:', !!updatedUser?.picture);
            console.log('Updated user picture length:', updatedUser?.picture?.length || 0);
            console.log('======================');
            
            if (result.count === 0) {
                console.warn('WARNING: No rows were updated!');
                throw new HttpErrors.NotFound('User not found or no changes made');
            }
        } catch (error: any) {
            console.error('=== UPDATE ERROR ===');
            console.error('Error:', error.message);
            console.error('===================');
            throw error;
        }
    }

    @patch('/account/token')
    async updateFcmToken(
        @param.header.string('authorization') authHeader: string,
        @requestBody({
            required: true,
            content: {
                'application/json': {
                    schema: {
                        type: 'object',
                        required: ['fcmToken'],
                        properties: {
                            fcmToken: { type: 'string'},
                        },
                    },
                },
            },
        })
        updateFcmTokenBody: UpdateFcmTokenBody,
    ) {
        if (!authHeader) {
            return { success: false };
        }
        
        const userProfile = await this.authService.verifyAuthHeader(authHeader);
        const userEmail = userProfile?.email;
        
        if (!userEmail) {
            return { success: false };
        }

        await this.userRepository.updateAll(
            { fcm_token: updateFcmTokenBody.fcmToken },
            { account_email: userEmail }
        );
        
        return { success: true };
    }

     @get('/account/storeOwnerNames')
    async getStoreOwnerNames(): Promise<Array<{full_name: string, email: string}>> {
        const accounts = await this.accountRepository.find({
            where: {role: 'store_owner'},
            fields: {full_name: true, email: true}
        });
        return accounts.map(account => ({full_name: account.full_name, email: account.email}));
    }
   
}