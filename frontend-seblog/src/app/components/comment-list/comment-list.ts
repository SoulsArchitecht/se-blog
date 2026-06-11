import { Component, inject, input, output, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CommentService } from '../../services/comment.service';
import { Comment, CommentCreateRequest } from '../../models/comment.model';
//import { PagedResponse } from '../../models/paged-response.model';
import { PaginatedResponse } from '../../models/api-response.model';
import { CommentItem } from '../comment-item/comment-item';
import { CommentForm } from '../comment-form/comment-form';
import { AuthService } from '../../services/auth.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-comment-list',
  standalone: true,
  imports: [
    CommonModule,
    CommentItem,
    CommentForm
  ],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.scss'
})
export class CommentList {
  private commentService = inject(CommentService);
  protected authService = inject(AuthService);

  postId = input.required<string>();
  viewMode = input<'list' | 'tree'>('list');

  commentAdded = output<Comment>();
  commentUpdated = output<{ id: string, content: string }>();
  commentDeleted = output<string>();

  comments = signal<Comment[]>([]);
  rootComments = signal<Comment[]>([]);
  isLoading = signal(true);
  loadingMore = signal(false);
  error = signal<string>('');
  currentPage = signal(0);
  lastPage = signal(false);
  totalComments = signal(0);
  
  constructor() {
    effect(() => {
      const postId = this.postId();
      if (postId) this.loadComments();
    }, { allowSignalWrites: true});
  }  

  toggleView(): void {
    const newMode = this.viewMode() === 'list' ? 'tree' : 'list';
    localStorage.setItem('commentViewMode', newMode);
    this.loadComments(0);
  }

  async loadComments(page = 0): Promise<void> {
    if (this.viewMode() === 'tree') {
      await this.loadTree();
      return;
    }

    try {
      this.isLoading.set(true);
      this.error.set('');

      const response = await firstValueFrom(
        this.commentService.getCommentsByPost(this.postId(), page, 20)
      );

      if (response?.content) {
        if(page === 0) {
          this.comments.set(response.content);
        } else {
          this.comments.update(prev => [...prev, ...response.content]);
        }
        this.currentPage.set(page);
        this.lastPage.set(response.last);
        this.totalComments.set(response.totalElements);
      } else if (page === 0) {
        this.comments.set([]);
        this.totalComments.set(0);
      }

      
    } catch (err: any) {
      this.error.set(err.message || 'Не удалось загрузить комментарии');
    } finally {
      this.isLoading.set(false);
      this.loadingMore.set(false);
    }
  }

  async loadTree(): Promise<void> {
    try {
      this.isLoading.set(true);
      this.error.set('');
      
      const tree = await firstValueFrom(
        this.commentService.getCommentTree(this.postId())
      );
      
      this.rootComments.set(tree || []);
      this.totalComments.set(this.countAllComments(tree || []));
    } catch (err: any) {
      this.error.set(err.message || 'Не удалось загрузить комментарии');
    } finally {
      this.isLoading.set(false);
    }
  }

  private countAllComments(comments: Comment[]): number {
    return comments.reduce((acc, c) => {
      return acc + 1 + (c.replies?.length ? this.countAllComments(c.replies) : 0);
    }, 0);
  }

  loadMore(): void {
    if (this.lastPage() || this.loadingMore()) return;
    this.loadingMore.set(true);
    this.loadComments(this.currentPage() + 1);
  }

  reload(): void {
    this.currentPage.set(0);
    this.loadComments(0);
  }

  async createComment(request: { content: string; parentId?: string }): Promise<void> {
    try {
      const newComment = await firstValueFrom(
        this.commentService.createComment(this.postId(), { 
          content: request.content, 
          parentId: request.parentId 
        })
      );
      
      if (newComment) {
        if (this.viewMode() === 'tree' && !request.parentId) {
          this.rootComments.update(comments => [newComment, ...comments]);
        } else {
          this.comments.update(comments => [newComment, ...comments]);
        }
        this.totalComments.update(n => n + 1);
        this.commentAdded.emit(newComment);
      }
    } catch (err: any) {
      this.error.set(err.message || 'Не удалось опубликовать комментарий');
    }
  }

  async handleEdit(event: { id: string; content: string }): Promise<void> {
    try {
      const updated = await firstValueFrom(
        this.commentService.updateComment(this.postId(), event.id, { content: event.content })
      );
      
      if (updated) {
        this.updateCommentInState(updated);
        this.commentUpdated.emit({ id: updated.id, content: updated.content });
      }
    } catch (err: any) {
      this.error.set(err.message || 'Не удалось обновить комментарий');
    }
  }

  async handleDelete(commentId: string): Promise<void> {
    if (!confirm('Удалить этот комментарий?')) return;
    
    try {
      await firstValueFrom(
        this.commentService.deleteComment(this.postId(), commentId)
      );
      
      this.removeCommentFromState(commentId);
      this.totalComments.update(n => Math.max(0, n - 1));
      this.commentDeleted.emit(commentId);
    } catch (err: any) {
      this.error.set(err.message || 'Не удалось удалить комментарий');
    }
  }

  private updateCommentInState(updated: Comment): void {
    const updateInArray = (arr: Comment[]): Comment[] => 
      arr.map(c => {
        if (c.id === updated.id) return { ...c, ...updated };
        if (c.replies?.length) {
          return { ...c, replies: updateInArray(c.replies) };
        }
        return c;
      });
    
    this.comments.update(comments => updateInArray(comments));
    this.rootComments.update(comments => updateInArray(comments));
  }

  private removeCommentFromState(commentId: string): void {
    const removeFromArray = (arr: Comment[]): Comment[] => 
      arr.filter(c => {
        if (c.id === commentId) return false;
        if (c.replies?.length) {
          c.replies = removeFromArray(c.replies);
        }
        return true;
      });
    
    this.comments.update(comments => removeFromArray(comments));
    this.rootComments.update(comments => removeFromArray(comments));
  }  
}
