export interface User {
  id: number;
  username: string;
  email: string;
  avatar?: string;
  bio?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest extends AuthRequest {
  username: string;
  confirmPassword: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}