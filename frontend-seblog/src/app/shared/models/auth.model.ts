export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}