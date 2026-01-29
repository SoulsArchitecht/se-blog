import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Post } from '../../models/post.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent implements OnInit {
  private postService = inject(PostService);
  private authService = inject(AuthService);
  
  posts = signal<Post[]>([]);
  isLoading = signal(true);
  currentPage = signal(1);
  hasMorePosts = signal(true);
  
  ngOnInit() {
    this.loadPosts();
  }
  
  loadPosts(page = 1): void {
    this.isLoading.set(true);
    
    this.postService.getPosts({ page, limit: 10 }).subscribe({
      next: (response) => {
        if (page === 1) {
          this.posts.set(response.data.items);
        } else {
          this.posts.set([...this.posts(), ...response.data.items]);
        }
        
        this.currentPage.set(page);
        this.hasMorePosts.set(page < response.data.totalPages);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
  
  loadMore(): void {
    this.loadPosts(this.currentPage() + 1);
  }
  
  get isAuthenticated() {
    return this.authService.isAuthenticated();
  }
}
