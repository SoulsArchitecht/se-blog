import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { PostCard } from '../../components/post-card/post-card';
import { Post } from '../../models/post.model';
import { PostType } from '../../models/post.model';
import { PostTypeService } from '../../services/post-type.service';
import { ContrastColorPipe } from '../../pipes/contrast-color.pipe';
import { CategoryIconPipe } from '../../pipes/category-icon.pipe';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    PostCard,
    ContrastColorPipe,
    CategoryIconPipe
  ],  
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent implements OnInit {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private postService = inject(PostService);
  private postTypeService = inject(PostTypeService);

  posts = signal<Post[]>([]);
  isLoading = signal(false);
  currentType = signal<string | null>(null);
  currentTypeData = signal<PostType | null>(null);
  currentPage = signal(1);
  totalPages = signal(1);
  hasMorePosts = signal(false);

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const slug = params.get('typeName');
      
      if (slug) {
        this.currentType.set(slug);
        this.currentPage.set(1);
        this.posts.set([]);
        this.loadTypeData(slug);
        this.loadPostsByTypeSlug(slug, 1);
      } else {
        this.currentType.set(null);
        this.currentTypeData.set(null);
        this.currentPage.set(1);
        this.posts.set([]);
        this.loadAllPosts(1);
      }
    });
  }

  loadTypeData(slug: string): void {
    this.postTypeService.getAllPostTypes().subscribe({
      next: (types) => {
        const type = types.find(t => t.slug === slug);
        this.currentTypeData.set(type || null);
      },
      error: (error) => {
        console.error('Error loading type:', error);
      }
    });
  }

  loadAllPosts(page: number): void {
    this.isLoading.set(true);
    
    this.postService.getPosts({ page, limit: 10 }).subscribe({
      next: (response) => {
        const newPosts = response.data.content || [];
        
        if (page === 1) {
          this.posts.set(newPosts);
        } else {
          this.posts.set([...this.posts(), ...newPosts]);
        }
        
        this.totalPages.set(response.data.totalPages);
        this.hasMorePosts.set(page < response.data.totalPages);
        this.currentPage.set(page);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading posts:', error);
        this.isLoading.set(false);
      }
    });
  }

  loadPostsByTypeSlug(slug: string, page: number): void {
    this.isLoading.set(true);
    
    this.postService.getPostsByTypeSlug(slug, page, 10).subscribe({
      next: (response) => {
        const newPosts = response.data.content || [];
        
        if (page === 1) {
          this.posts.set(newPosts);
        } else {
          this.posts.set([...this.posts(), ...newPosts]);
        }
        
        this.totalPages.set(response.data.totalPages);
        this.hasMorePosts.set(page < response.data.totalPages);
        this.currentPage.set(page);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading posts by type slug:', error);
        this.isLoading.set(false);
      }
    });
  }  

  loadMore(): void {
    const nextPage = this.currentPage() + 1;
    const slug = this.currentType();
    
    if (slug) {
      this.loadPostsByTypeSlug(slug, nextPage);
    } else {
      this.loadAllPosts(nextPage);
    }
  }  
  
  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}
