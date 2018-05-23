import { Component } from '@angular/core';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent {
  fileName: string;
  text: string;

  constructor() {

  }

  onScrollToTop(): void {
    document.getElementById('file-viewer-content').scrollTop = 0;
  }

  onScrollToBottom(): void {
    document.getElementById('file-viewer-content').scrollTop = document.getElementById('file-viewer-content').scrollHeight;
  }
}
