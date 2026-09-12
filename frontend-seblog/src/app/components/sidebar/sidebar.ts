import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { PostTypeService } from '../../services/post-type.service';
import { PostType } from '../../models/post.model';
import { ContrastColorPipe } from '../../pipes/contrast-color.pipe';
import { CategoryIconPipe } from '../../pipes/category-icon.pipe';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    RouterLinkActive,
    ContrastColorPipe,
    CategoryIconPipe
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit {
  private authService = inject(AuthService);
  private postTypeService = inject(PostTypeService);
  private route = inject(ActivatedRoute);

  postTypes = signal<PostType[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    this.loadPostTypes();
  }

  loadPostTypes(): void {
    this.isLoading.set(true);

    this.postTypeService.getAllPostTypes().subscribe({
      next: (types) => {
        this.postTypes.set(types);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading post types:', error);
        this.isLoading.set(false);
      }
    });
  }

  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}
