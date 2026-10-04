import { CommonModule } from '@angular/common';
import { Component, inject, input, output, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
//import { AvatarUrlPipe } from '../../pipes/avatar-url.pipe';
import Quill from 'quill';
import { QuillModule, QuillModules } from 'ngx-quill';
import { ApiService } from '../../services/api.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-post-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    QuillModule,
    //AvatarUrlPipe
  ],
  templateUrl: './post-form.html',
  styleUrl: './post-form.scss'
})
export class PostForm implements OnInit {
  private fb = inject(FormBuilder);
  private apiService = inject(ApiService);

  mode = input<'create' | 'edit'>('create');
  initialData = input<any>(null);

  submitted = output<any>();
  cancelled = output<void>();

  postForm!: FormGroup;
  isSubmitting = signal(false);

  categories = ['Hardware', 'Software', 'Music', 'Humor', 'Поэзия', 'Проза'];

  editorModules: QuillModules = {
    toolbar: {
      container: [
        [{header: [1, 2, 3, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{list: 'ordered'}, {list: 'bullet'}],
        ['link', 'image', 'video'],
        ['clean']
      ]
    }
  };

  ngOnInit(): void {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      content: ['', [Validators.required, Validators.minLength(50)]],
      postType: ['', Validators.required],
      tags: [''],
      isPublished: [true],
      customSlug: ['']      
    });

    if (this.mode() === 'edit' && this.initialData()) {
      const data = this.initialData();

      const rawTags = data.tagNames || data.tags || [];

      let initialTags = '';

      if (Array.isArray(rawTags)) {
        if (rawTags.length > 0 && typeof rawTags[0] === 'object') {
          initialTags = rawTags.map((tag: any) => tag.name || tag).join(', ');
        } else {
          initialTags = rawTags.join(', ');
        }
      } else if (typeof rawTags === 'string') {
        initialTags = rawTags;
      }      

      this.postForm.patchValue({
        title: data.title,
        content: data.content,
        postType: data.postTypeName || data.type?.name,
        tags: initialTags,
        isPublished: data.status === 'PUBLISHED',
        customSlug: data.slug || ''
      });
    }
  }

  onEditorCreated(quill: any): void {
    const toolbar = quill.getModule('toolbar');
    toolbar.addHandler('image', () => this.imageHandler(quill));
  }

  private imageHandler(quill: any): void {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';

    fileInput.onchange = async () => {
      const file = fileInput.files?.[0];
      if (!file) return;

      const range = quill.getSelection(true);
      if (!range) return;

      quill.insertText(range.index, 'Загрузка изображения...', { italic: true });

      try {
        const formData = new FormData();
        formData.append('image', file);
        
        const response: any = await this.apiService.post('/uploads/post-image', formData).toPromise();
        let url = response?.data?.url || response?.url;

        quill.deleteText(range.index, 'Загрузка изображения...'.length);
        quill.insertEmbed(range.index, 'image', url);
        quill.setSelection(range.index + 1);
      } catch (error) {
        console.error('Ошибка загрузки изображения', error);
        quill.deleteText(range.index, 'Загрузка изображения...'.length);
        alert('Не удалось загрузить изображение');
      }
    };

    fileInput.click();
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const formValue = this.postForm.value;

    let finalSlug = formValue.customSlug?.trim();
    if (this.mode() === 'edit' && (!finalSlug || finalSlug.length === 0)) {
      finalSlug = this.initialData()?.slug || undefined;
    }

    let rawTagsArray = formValue.tags
      ? formValue.tags.split(',').map((t: string) => t.trim()).filter(Boolean)
      : [];

    let finalTagNames = rawTagsArray;
    if (this.mode() === 'edit' && rawTagsArray.length === 0) {
      const originalTags = this.initialData()?.tagNames;
      finalTagNames = Array.isArray(originalTags) ? originalTags : (originalTags ? [originalTags] : undefined);
    }

    const postData = {
      title: formValue.title,
      content: formValue.content,
      postTypeName: formValue.postType, 
      status: formValue.isPublished ? 'PUBLISHED' : 'DRAFT',
      slug: finalSlug,                  
      tagNames: finalTagNames
    };

    console.log('📤 Отправляем на бэкенд:', postData);
    this.submitted.emit(postData);
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
