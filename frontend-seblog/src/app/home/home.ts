import { Component, inject } from '@angular/core';
import { ApiService } from '../core/services/api.service';
import { PostSummary } from '../shared/models/post.model';

import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  // template: `
  //   @if (loading) {
  //     <div class="loading">
  //       <div class="spinner"></div>
  //     </div>
  //   }

  //   @defer (when posts.length > 0) {
  //     <main class="posts-grid">
  //       @for (post of posts; track post.id) {
  //         <article class="post-card">
  //           <div class="post-header">
  //             <span class="post-type" [style.background]="post.type.colorHex">
  //               {{ post.type.name }}
  //             </span>
  //             <h2><a [routerLink]="['/post', post.slug]">{{ post.title }}</a></h2>
  //           </div>

  //           <div class="post-meta">
  //             <img [src]="post.author.avatarUrl" alt="Avatar" class="avatar">
  //             <span class="author">{{ post.author.displayName }}</span>
  //             <time class="date">{{ post.createdAt | date:'dd.MM.yyyy' }}</time>
  //           </div>

  //           <div class="post-stats">
  //             <span><i class="icon-eye"></i> {{ post.viewCount }}</span>
  //             <span><i class="icon-thumb-up"></i> {{ post.upvotes }}</span>
  //             <span><i class="icon-chat"></i> {{ post.commentCount }}</span>
  //           </div>
  //         </article>
  //       }
  //     </main>
  //   } @placeholder {
  //     <div class="loading"><div class="spinner"></div></div>
  //   }
  // `,
  styleUrls: ['./home.scss'],
  templateUrl: './home.html',
  standalone: true,
  imports: [
    DatePipe,   
    RouterLink  
  ]
})
export class Home {
  posts: PostSummary[] = [];
  loading = true;

  private api = inject(ApiService);

  constructor() {
    this.loadPosts();
  }

  loadPosts(): void {
    this.api.getPosts().subscribe({
      next: (data) => {
        this.posts = data.content || [];
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}