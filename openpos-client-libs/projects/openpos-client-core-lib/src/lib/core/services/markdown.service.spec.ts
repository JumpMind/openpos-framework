import { MarkdownService } from './markdown.service';

describe('MarkdownService', () => {

    let markdownService: MarkdownService;

    beforeEach(() => {
        markdownService = new MarkdownService();
    });

    it('simple transform of text to bold', () => {
        expect(markdownService.transform('**bold text**')).toBe('<p><strong>bold text</strong></p>');
    });

    it('custom filter for transforming text to bold', () => {
        expect(markdownService.transform('&bbold text&')).toBe('<p><strong>bold text</strong></p>');
    });

    it('centering of text', () => {
        expect(markdownService.transform('>>center me<<')).toBe('<p class="text-center">center me</p>');
    });

    /// When centering need to use &b for bold, won't work with showdown otherwise
    it('centered bold text', () => {
        expect(markdownService.transform('>>&bcenter me&<<')).toBe('<p class="text-center"><strong>center me</strong></p>');
    });

    it('large text', () => {
        expect(markdownService.transform('^^make text large^^')).toBe('<p><span class="text-lg">make text large</span></p>');
    });

    it('warn text', () => {
        expect(markdownService.transform('&&Wwarning&&')).toBe('<p><span class="warn">warning</span></p>');
        expect(markdownService.transform('&&wwarning&&')).toBe('<p><span class="warn">warning</span></p>');
    });

    it('primary color text', () => {
        expect(markdownService.transform('&&Ptext in primary color&&')).toBe('<p><span class="primary">text in primary color</span></p>');
        expect(markdownService.transform('&&ptext in primary color&&')).toBe('<p><span class="primary">text in primary color</span></p>');
    });

    it('accent color text', () => {
        expect(markdownService.transform('&&Atext in accent color&&')).toBe('<p><span class="accent">text in accent color</span></p>');
        expect(markdownService.transform('&&atext in accent color&&')).toBe('<p><span class="accent">text in accent color</span></p>');
    });

    it('large warn centered text', () => {
        expect(markdownService.transform('>>^^&&wlarge centered warn text&&^^<<')).toBe('<p class="text-center"><span class="text-lg"><span class="warn">large centered warn text</span></span></p>');
    });

    it('large italic text', () => {
        expect(markdownService.transform('^^*large italic text*^^')).toBe('<p><span class="text-lg"><em>large italic text</em></span></p>');
    });

    // When centering need to use &b for bold, won't work with showdown otherwise
    it('centered large bold text', () => {
        expect(markdownService.transform('>>^^&bLine 1&^^<<')).toBe('<p class="text-center"><span class="text-lg"><strong>Line 1</strong></span></p>');
    });

    it('large bold text', () => {
        expect(markdownService.transform('^^**Line 1**^^')).toBe('<p><span class="text-lg"><strong>Line 1</strong></span></p>');
    });

    it('unordered list', () => {
        expect(markdownService.transform('* Item 1\n* Item 2\n * Item 3\n')).toBe(`<ul>\n<li>Item 1</li>\n<li>Item 2</li>\n<li>Item 3</li>\n</ul>`);
    });

});
