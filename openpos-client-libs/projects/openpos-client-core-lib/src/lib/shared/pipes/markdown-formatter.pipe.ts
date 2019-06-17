import { Pipe, PipeTransform } from '@angular/core';
import { MarkdownService } from '../../core/services/markdown.service';

/**
 * See MarkdownService for details on supported custom markdown
 */
@Pipe({ name: 'markdownFormatter' })
export class MarkdownFormatterPipe implements PipeTransform {

    constructor(private markdownService: MarkdownService) {
    }

    transform(value: string): string {
        return this.markdownService.transform(value);
    }
}
