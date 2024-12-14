import {Component, EventEmitter, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {ArticleCreationComponent} from "../../news/article-creation/article-creation.component";

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

    constructor(public articleCreation: ArticleCreationComponent) {
    }
    @Output() imagesUploaded = new EventEmitter<Event>();

    get imageFiles(): File[] {
        return this.articleCreation.imageFiles;
    }
}
