export interface PostSummary {
  id: string;
  title: string;
  slug: string;
  content?: string;
  author: UserSummary;
  type: PostType;
  createdAt: string;
  viewCount: number;
  upvotes: number;
  commentCount: number;
}

export interface PostType {
  id: string;
  name: string;
  slug: string;
  colorHex: string;
  icon: string;
}

export interface UserSummary {
  id: string;
  username: string;
  displayName: string;
  avatarUrl: string;
  createdAt: string;
}