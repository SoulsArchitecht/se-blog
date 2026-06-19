export interface VoteRequest {
    type: 'LIKE' | 'DISLIKE';
}

export interface VoteStats {
    likesCount: number;
    dislikesCount: number;
    totalScore: number;
    userVote: 'LIKE' | 'DISLIKE' | null;
}