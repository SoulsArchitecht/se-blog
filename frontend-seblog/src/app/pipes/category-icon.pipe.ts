import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'categoryIcon',
  standalone: true
})
export class CategoryIconPipe implements PipeTransform {
  private readonly iconMap: Record<string, string> = {
    'cpu': '💻',
    'code': '⌨️',
    'music-note': '🎵',
    'laugh': '😄',
    'poem': '✍️',
    'book-open': '📖',
    'default': '📌'
  };

  transform(iconName?: string | null): string {
    if (!iconName) return this.iconMap['default'];
    return this.iconMap[iconName] || this.iconMap['default'];
  }
}