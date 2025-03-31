import {Component, EventEmitter, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {ArticleCreationService} from "../../news/article-creation/article-creation.service";

@Component({
  selector: 'app-file-upload-button',
  standalone: true,
    imports: [
        NgForOf,
        MatButton
    ],
  templateUrl: './file-upload-button.component.html',
  styleUrl: './file-upload-button.component.scss'
})
export class FileUploadButtonComponent {

    constructor(public articleCreationService: ArticleCreationService) {
    }
    @Output() imagesUploaded = new EventEmitter<Event>();

    get imageFiles(): File[] {
        return this.articleCreationService.files;
    }

    onFileChange(event: Event) {
        const target: HTMLInputElement = event.target as HTMLInputElement;

        if(target.files)
        {
            this.articleCreationService.files = Array.from(target.files);
        }
    }
}
