export interface UserSummary {
    id: string;
    username: string;
    email: string;
    role: string;
}

export interface UserProfile {
    id: string;
    user: UserSummary;
    displayName: string | null;
    firstName: string | null;
    lastName: string | null;
    birthDate: Date | null;
    phone: string | null;
    avatarUrl: string | null;
    bio: string | null;
    locationAt: string | null;
    updatedAt: Date | null;
    lastLoginAt: Date | null;
    rating: number;
    optionalEmail: string | null;
}

export interface UserProfileUpdate {
    displayName?: string;
    firstName?: string;
    lastName?: string;
    birthDate?: Date;
    phone?: string;
    avatarUrl?: string;
    bio?: string;
    locationAt?: string;
    optionalEmail?: string;
}

