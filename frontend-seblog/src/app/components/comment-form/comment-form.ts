import { Component, inject, input, output, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup  } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-comment-form',
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  standalone: true,
  templateUrl: './comment-form.html',
  styleUrl: './comment-form.scss'
})
export class CommentForm implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  // Inputs
  placeholder = input<string>('Напишите комментарий...');
  submitLabel = input<string>('Отправить');
  cancelLabel = input<string>('');
  rows = input<number>(4);
  initialContent = input<string>('');
  isReply = input<boolean>(false);
  parentId = input<string | undefined>(undefined);

  // Outputs
  commentSubmitted = output<{ content: string; parentId?: string }>();
  cancel = output<void>();

  // State
  isSubmitting = signal(false);

  // Form объявляем как undefined
  form!: FormGroup;

  ngOnInit(): void {
    this.form = this.fb.group({
      content: [this.initialContent(), [
        Validators.required, Validators.minLength(2), Validators.maxLength(1000)]]
    });

    // if (this.initialContent()) {
    //   this.form.patchValue({ content: this.initialContent() });
    // }
  }

  onSubmit(): void {
    if (this.isSubmitting() || this.form.invalid || !this.authService.isAuthenticated()) {
      this.form.markAllAsTouched();
      return;
    }
    
    this.isSubmitting.set(true);

    const rawContent = this.form.value.content;
    const cleanContent = typeof rawContent === 'string' ? rawContent.trim() : '';
  
    if (!cleanContent) {
      this.isSubmitting.set(false);
      return;
    }

    const parentIdValue = this.parentId();
    const cleanParentId = (parentIdValue && parentIdValue !== 'undefined' && parentIdValue !== 'null')
      ? parentIdValue
      : undefined;

    this.commentSubmitted.emit({ 
      //content: this.form.value.content!,
      //parentId: this.parentId()
      content: cleanContent,
      parentId: cleanParentId
    });

    //this.reset();
    this.form.reset();
    this.form.markAsPristine();
    this.form.markAsUntouched();

    setTimeout(() => this.isSubmitting.set(false), 500);
  }

  onCancel(): void {
    this.form.reset();
    this.cancel.emit();
  }

  reset(): void {
    this.form.reset();
    this.form.markAsPristine();
    this.form.markAsUntouched();
    setTimeout(() => this.isSubmitting.set(false), 500);
  }

  setSubmitting(value: boolean): void {
    this.isSubmitting.set(value);
  }
}
