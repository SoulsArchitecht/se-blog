import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'contrastColor',
    standalone: true
})
export class ContrastColorPipe implements PipeTransform {
    transform(backgroundColor?: string | null): string {
        if (!backgroundColor) return '#1f2937';

        const hex = backgroundColor.replace('#', '');
        const r = parseInt(hex.substring(0, 2), 16);
        const g = parseInt(hex.substring(2, 4), 16);
        const b = parseInt(hex.substring(4, 6), 16);

        const brightness = (r * 299 + g * 587 + b * 114) / 1000;
        return brightness > 128 ? '#1f2937' : '#fffff';
    }
}