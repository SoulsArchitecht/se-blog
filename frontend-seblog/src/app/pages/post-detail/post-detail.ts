import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Post } from '../../models/post.model';
import { Comment } from '../../models/comment.model';
import { UserProfileService } from '../../services/user-profile.service';
import { CommentList } from '../../components/comment-list/comment-list';
import { PostVoteService } from '../../services/post-vote.service';
import { VoteStats } from '../../models/vote.model';
import { VoteButtons } from '../../components/vote-buttons/vote-buttons';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    CommentList,
    VoteButtons
  ],
  templateUrl: './post-detail.html',
  styleUrls: ['./post-detail.scss']
})
export class PostDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  router = inject(Router);
  private postService = inject(PostService);
  private authService = inject(AuthService);
  private userProfileService = inject(UserProfileService);
  private fb = inject(FormBuilder);
  private postVoteService = inject(PostVoteService);
  postVoteStats = signal<VoteStats | null>(null);
  isVoting = signal(false);
  
  post = signal<Post | null>(null);
  isLoading = signal(true);
  error = signal<string>('');
  
  commentForm: FormGroup;
  isSubmittingComment = signal(false);
  
  constructor() {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(5)]]
    });
  }
  
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadPost(String(params['id']));
    });
  }

  onCommentAdded(comment: Comment): void {
    this.post.update(p => p ? {
      ...p,
      commentsCount: (p.commentsCount || 0) + 1
    } : p);
  }

  onCommentUpdated(event: { id:string; content: string }): void {
    this.post.update(p => p ? {
      ...p,
      commentsCount: Math.max(0, (p.commentsCount || 0) - 1)
    } : p);
  }

  onCommentDeleted(commentId: string ): void {
    this.post.update(p => p ? {
      ...p,
      commentsCount: Math.max(0, (p.commentsCount || 0) - 1)
    } : p);
  }
  
  loadPost(id: string): void {
    this.isLoading.set(true);
    
    this.postService.getPost(id).subscribe({
      next: (response) => {
        this.post.set(response.data);
        this.isLoading.set(false);
        this.loadPostVoteStats();

        if (this.post()?.author.id) {
          this.userProfileService.getUserProfile(this.post()!.author.id).subscribe({
            next: (profileResponse) => {
              const avatarUrl = profileResponse.data?.avatarUrl ?? undefined;
              this.post.update(p => ({
                ...p!,
                author: {
                  ...p!.author,
                  avatar: avatarUrl
                }
              }));
            }
          });
        }
      },
      error: (error) => {
        this.error.set(error.message || 'Пост не найден');
        this.isLoading.set(false);
      }
    });
  }
  
  // addComment(): void {
  //   if (this.commentForm.invalid || !this.post()) return;
    
  //   this.isSubmittingComment.set(true);
    
  //   // Здесь будет логика добавления комментария через API
  //   setTimeout(() => {
  //     const newComment: Comment = {
  //       //id: Math.random(),
  //       id: "1",
  //       content: this.commentForm.value.content,
  //       author: {
  //         id: this.authService.user()?.id || "0",
  //         username: this.authService.user()?.username || 'Аноним'
  //       },
  //       createdAt: new Date()
  //     };
      
  //     // Обновляем пост с новым комментарием
  //     this.post.set({
  //       ...this.post()!,
  //       commentsCount: this.post()!.commentsCount + 1
  //     });
      
  //     this.commentForm.reset();
  //     this.isSubmittingComment.set(false);
  //   }, 1000);
  // }
  
  deletePost(): void {
    if (!this.post() || !confirm('Удалить этот пост?')) return;
    
    this.postService.deletePost(this.post()!.id).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error deleting post:', error);
      }
    });
  }
  
  get isAuthor(): boolean {
    return this.post()?.author.id === this.authService.user()?.id;
  }
  
  get canEdit(): boolean {
    return this.isAuthor || this.authService.user()?.username === 'admin';
  }

  getAvatarUrl(avatarFilename?: string | null): string {
    if (!avatarFilename) {
      return '/assets/default-avatar.png';
    }
    return `/api/v1/uploads/${avatarFilename}`;
  }

  loadPostVoteStats(): void {
    if (!this.post()) return;

    this.postVoteService.getPostVoteStats(this.post()!.id).subscribe({
      next: (stats) => this.postVoteStats.set(stats),
      error: (error) => console.error('Error loading post vote ststs: ', error)
    });
  }

  handlePostVote(voteType: 'LIKE' | 'DISLIKE' | 'REMOVE'): void {
    if (!this.post()) return;

    this.isVoting.set(true);

    const request$ = voteType === 'REMOVE'
    ? this.postVoteService.removePostVote(this.post()!.id)
    : this.postVoteService.votePost(this.post()!.id, { type: voteType });

    request$.subscribe ({
      next: (stats) => {
        this.postVoteStats.set(stats);
        this.post.update(p => p ? { ...p, likesCount: stats.likesCount } : p);
        this.isVoting.set(false);
      },
      error: (error) => {
        console.error('Error voting post:', error);
        this.isVoting.set(false);
      }
    });
  }
}