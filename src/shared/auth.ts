export interface AuthUser {
  username: string;
  uuid: string;
}

export interface AuthRegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthLoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  ok: boolean;
  message: string;
  user?: AuthUser;
}
