export interface User {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  bio?: string;
  createdAt: Date;
  updatedAt: Date;
  role: string;
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
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface UserProfile {
  id: string;
  user: User;
  displayName: string;
  firstName: string;
  lastName: string;
  birthDate: Date;
  phone: string;
  avatarUrl: string;
  bio: string;
  location: string;
  updatedAt: Date;
  lastLoginAt: Date;
  rating: number;
  optionalEmail: string;
}

// export interface AuthResponse {
//   accessToken: string;
// }