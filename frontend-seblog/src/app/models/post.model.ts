export interface Author {
  id: string;
  username: string;
  avatar?: string;
}

// export interface Comment {
//   id: string;
//   content: string;
//   author: Author;
//   createdAt: Date;
// }

export interface Post {
  id: string;
  title: string;
  content: string;
  //excerpt: string;
  slug: string;
  status: string;
  author: Author;
  postType: string;
  tags: string[];
  commentsCount: number;
  likesCount: number;
  viewsCount: number;
  isPublished: boolean;
  createdAt: Date;
  updatedAt: Date;
  publishedAt?: Date;
  comments: Comment[];
}

export interface PostCreate {
  content: string;
  postTypeName: string;
  title: string;
  status: string;
  tagNames: string[];
  customSlug?: string;
}

export interface PostUpdate extends Partial<PostCreate> {
  isPublished?: boolean;
}

export interface PostVoteStats {
  likesCount: number,
  dislikeCount: number,
  totalScore: number,
  userVote: 'LIKE' | 'DISLIKE' | null;
}