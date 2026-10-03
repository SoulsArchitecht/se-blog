import { CommonModule } from '@angular/common';
import { Component, inject, input, output, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
//import { AvatarUrlPipe } from '../../pipes/avatar-url.pipe';
import Quill from 'quill';
import { QuillModule, QuillModules } from 'ngx-quill';
import { ApiService } from '../../services/api.service';

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

  categories = ['Hardware', 'Software', 'Music', 'Poetry', 'Proza'];

  editorModules: QuillModules = {
    toolbar: {
      container: [
        [{header: [1, 2, 3, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{list: 'ordered'}, {list: 'bullet'}],
        ['link', 'image', 'video'],
        ['clean']
      ],
      handlers: {
        image: this.imageHandler.bind(this)
      }
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
      this.postForm.patchValue({
        title: data.title,
        content: data.content,
        postType: data.postTypeName || data.type?.name,
        tags: data.tagNames?.join(', ') || '',
        isPublished: data.status === 'PUBLISHED',
        customSlug: data.slug || ''
      });
    }
  }

  private imageHandler(): void {
    const quill = (this as any).quillEditor?.quill;
    if (!quill) return;

    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';

    fileInput.onchange = async () => {
      const file = fileInput.files?.[0];
      if (!file) return;

      const range = quill.getSelection(true);
      quill.insertText(range.index, 'Загрузка изображения...',  { italic: true });

      try {
        const formData = new FormData();
        formData.append('image', file);
        const response: any = await this.apiService.post('/uploads/post-image', formData).toPromise();
        const url = response?.data?.url || response?.url;

        quill.deleteText(range.index, 'Загрузка изображения...'.length);
        quill.insertEmbed(range.index, 'image', url);
        quill.setSelection(range.index + 1);
      } catch (error) {
        console.error('Ошибка загрузки изображения', error);
        quill.deleteText(range.index, 'Загрузка изображения...'.length);
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

    const postData = {
      content: formValue.content,
      postTypeName: formValue.postType,
      title: formValue.title,
      status: formValue.isPublished ? 'PUBLISHED' : 'DRAFT',
      tagNames: formValue.tags.split(',').map((t: string) => t.trim()).filter(Boolean),
      customSlug: formValue.customSlug || undefined,
    };

    this.submitted.emit(postData);
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
