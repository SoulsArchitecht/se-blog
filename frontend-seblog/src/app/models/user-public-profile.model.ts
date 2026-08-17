export interface UserPublicProfile {
    id: string;
    username: string;
    displayName: string | null;
    location: string | null;
    avatarUrl: string | null;
    bio: string | null;
    memberSince: string;
    postCount: number;
    commentCount: number;
    rating: number;
}