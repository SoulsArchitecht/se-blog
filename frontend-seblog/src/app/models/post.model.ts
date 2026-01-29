export interface Author {
  id: number;
  username: string;
  avatar?: string;
}

export interface Comment {
  id: number;
  content: string;
  author: Author;
  createdAt: Date;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  excerpt: string;
  slug: string;
  author: Author;
  category: string;
  tags: string[];
  commentsCount: number;
  likesCount: number;
  viewsCount: number;
  isPublished: boolean;
  createdAt: Date;
  updatedAt: Date;
  publishedAt?: Date;
}

export interface PostCreate {
  title: string;
  content: string;
  category: string;
  tags: string[];
}

export interface PostUpdate extends Partial<PostCreate> {
  isPublished?: boolean;
}