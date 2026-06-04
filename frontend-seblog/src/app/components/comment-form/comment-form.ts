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

  // Outputs
  submit = output<{ content: string; parentId?: string }>();
  cancel = output<void>();

  // State
  isSubmitting = signal(false);

  // Form объявляем как undefined
  form!: FormGroup;

  ngOnInit(): void {
    this.form = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(5)]]
    });

    if (this.initialContent()) {
      this.form.patchValue({ content: this.initialContent() });
    }
  }

  onSubmit(): void {
    if (this.form.invalid || !this.authService.isAuthenticated()) return;
    
    this.isSubmitting.set(true);
    this.submit.emit({ 
      content: this.form.value.content!,
      parentId: this.isReply() ? undefined : undefined
    });
  }

  reset(): void {
    this.form.reset();
    this.isSubmitting.set(false);
  }

  setSubmitting(value: boolean): void {
    this.isSubmitting.set(value);
  }
}
