import { repository } from '@loopback/repository';
import { HttpErrors, post, requestBody } from '@loopback/rest';
import { AccountRepository, UserRepository, StoreOwnerRepository } from '../repositories';
import { AuthService, Credentials } from '../services/auth.service';
import { hash } from 'bcryptjs';
import { service } from '@loopback/core';

export class AuthController {
    constructor(
        @repository(UserRepository)
        public userRepository: UserRepository,
        @repository(AccountRepository)
        public accountRepository: AccountRepository,
        @repository(StoreOwnerRepository)
        public storeOwnerRepository: StoreOwnerRepository,
        @service(AuthService)
        public authService: AuthService,
    ) { }

    @post('/auth/signup')
    async signUp(
        @requestBody({
            required: true,
            content: {
                'application/json': {
                    schema: {
                        type: 'object',
                        required: ['email', 'password', 'fcmToken', 'fullName'],
                        properties: {
                            email: { type: 'string', format: 'email' },
                            password: { type: 'string', minLength: 8 },
                            fcmToken: { type: 'string' },
                            fullName: { type: 'string' },
                            isStoreOwner: { type: 'boolean' },
                            country: { type: 'string' },
                            city: { type: 'string' },
                            postalCode: { type: 'string' },
                            streetName: { type: 'string' },
                            streetNumber: { type: 'string' },
                        },
                    },
                },
            },
        })
        credentials: Credentials,
    ) {
        const userAlreadyExistsError = "User with this email already exists";

        const foundAccount = await this.accountRepository.findOne({
            where: { email: credentials.email.toLowerCase().trim() }
        })
        if (foundAccount) {
            throw new HttpErrors[400](userAlreadyExistsError);
        }

        const newAccount = await this.accountRepository.create({
            email: credentials.email.toLowerCase().trim(),
            password: await hash(credentials.password, 10),
            role: credentials.isStoreOwner ? 'store_owner' : 'customer',
            full_name: credentials.fullName,
        })

        const newUser = await this.userRepository.create({
            fcm_token: credentials.fcmToken,
            notifications: false,
            account_email: newAccount.email,
        })

        // Create store owner record if isStoreOwner is true
        if (credentials.isStoreOwner) {
            await this.storeOwnerRepository.create({
                account_email: newAccount.email,
                country: credentials.country,
                city: credentials.city,
                postal_code: credentials.postalCode,
                street_name: credentials.streetName,
                street_number: credentials.streetNumber,
            });
        }
        
        return { success: true };
    }

    @post('/auth/login')
    async login(
        @requestBody({
            required: true,
            content: {
                'application/json': {
                    schema: {
                        type: 'object',
                        required: ['email', 'password'],
                        properties: {
                            email: {type: 'string', format: 'email'},
                            password: {type: 'string'},
                        },
                    },
                },
            },
        })
        credentials: Credentials,
    ) {
        const account = await this.authService.verifyCredentials(credentials);
        const userProfile = this.authService.convertToUserProfile(account);
        const token = await this.authService.generateToken(userProfile);

        return {token};
    }
}