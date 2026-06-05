export interface CommentAuthor {
    id: string;
    username: string;
    avatar?: string | null;
}

export interface Comment {
    id: string;
    content: string;
    author: CommentAuthor;
    createdAt: string;
    updatedAt?: string | null;
    postId: string;
    replies?: Comment[];
    isDeleted?: boolean;
    canDeleted?: boolean;
    canEdit?: boolean;
    canDelete?: boolean;
}

export interface CommentCreateRequest {
    content: string;
    parentId?: string | null;
}

export interface CommentPage {
    content: Comment[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
}