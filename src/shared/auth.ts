export interface AuthUser {
  id: string;
  username: string;
  cmcUuid: string;
  createdAt?: string;
}

export interface AuthRegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthLoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface AuthSessionResponse {
  user: AuthUser;
  accessToken: string;
  refreshToken: string;
}
