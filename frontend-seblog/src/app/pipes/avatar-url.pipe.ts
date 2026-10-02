import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'avatarUrl',
  standalone: true,
  pure: true
})
export class AvatarUrlPipe implements PipeTransform {
  transform(avatarUrl: string | null | undefined): string {
    if (!avatarUrl) {
      return '/assets/default-avatar.png'; 
    }

    if (avatarUrl.startsWith('https://')) {
      return avatarUrl;
    }

    if (avatarUrl.startsWith('/api/v1/uploads/')) {
      return avatarUrl;
    }

    return `/api/v1/uploads/avatars/${avatarUrl}`;
  }
}