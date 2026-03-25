export interface Author {
  id: string;
  username: string;
  avatar?: string;
}

export interface Comment {
  id: string;
  content: string;
  author: Author;
  createdAt: Date;
}

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
}

export interface PostCreate {
  title: string;
  content: string;
  postType: string;
  tags: string[];
}

export interface PostUpdate extends Partial<PostCreate> {
  isPublished?: boolean;
}