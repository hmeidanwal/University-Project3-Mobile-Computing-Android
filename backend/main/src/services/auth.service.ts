import { TokenService, UserService } from "@loopback/authentication";
import { Account } from "../models";
import { securityId, UserProfile } from "@loopback/security";
import { repository } from "@loopback/repository";
import { AccountRepository } from "../repositories";
import { HttpErrors } from "@loopback/rest";
import { compare } from "bcryptjs";
import { TokenServiceBindings } from "@loopback/authentication-jwt";
import { inject } from "@loopback/core";

export type Credentials = {
    email: string;
    password: string;
    fcmToken: string;
    fullName: string;
    isStoreOwner?: boolean;
    country?: string;
    city?: string;
    postalCode?: string;
    streetName?: string;
    streetNumber?: string;
  };

export class AuthService implements UserService<Account, Credentials> {
  constructor(
    @repository(AccountRepository) public accountRepository: AccountRepository,
    @inject(TokenServiceBindings.TOKEN_SERVICE)
    public tokenService: TokenService,
  ) {}
  async verifyCredentials(credentials: Credentials): Promise<Account> {
    const invalidCredentialsError = 'Invalid email or password.';

    const foundAccount = await this.accountRepository.findOne({
      where: {email: credentials.email.toLowerCase().trim()}
    })
    if (!foundAccount) {
      throw new HttpErrors[400](invalidCredentialsError);
    }

    const passwordMatches = await compare(
      credentials.password,
      foundAccount.password,
    )

    if (!passwordMatches) {
      throw new HttpErrors.Unauthorized(invalidCredentialsError);
    }

    return foundAccount;
  }
  
  convertToUserProfile(account: Account): UserProfile {
    return {
      [securityId]: account.email,
      name: account.full_name,
      email: account.email,
    };
  }

  async generateToken(userProfile: UserProfile): Promise<string> {
    return this.tokenService.generateToken(userProfile);
  }

  async verifyAuthHeader(authHeader: string): Promise<UserProfile> {
    const token = authHeader.split(' ')[1];
    if (!token) {
      throw new HttpErrors.Unauthorized('Invalid authorization header');
    }
    return this.tokenService.verifyToken(token);
  }
}
