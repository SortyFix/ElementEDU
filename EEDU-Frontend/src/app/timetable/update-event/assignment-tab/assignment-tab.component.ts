import {Component, input, InputSignal} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AssignmentModel} from "../../../user/courses/appointment/entry/assignment-model";

@Component({
  selector: 'app-assignment-tab',
  standalone: true,
    imports: [
        FormsModule,
        NgForOf,
        MatListItemLine,
        MatListItemTitle,
        MatListItem,
        MatList,
        ReactiveFormsModule,
        NgIf,
    ],
  templateUrl: './assignment-tab.component.html',
  styleUrl: './assignment-tab.component.scss'
})
export class AssignmentTabComponent {

    public readonly editing: InputSignal<boolean> = input<boolean>(false);
    public readonly assignment: InputSignal<AssignmentModel | undefined> = input<AssignmentModel | undefined>();
    isDragging: boolean = false;
    uploadedFiles: File[] = [];

    readonly MAX_FILES = 3;

    protected onDragOver(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.isDragging = true;
    }

    protected onDragLeave(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.isDragging = false;
    }

    protected onDrop(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.isDragging = false;

        if (event.dataTransfer && event.dataTransfer.files.length > 0) {
            const files: File[] = Array.from(event.dataTransfer.files);
            this.addFiles(files);
        }
    }

    // Handle file selection from input
    protected onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files) {
            const files: File[] = Array.from(input.files);
            this.addFiles(files);
        }
    }

    private addFiles(files: File[]): void {
        if (this.uploadedFiles.length + files.length > this.MAX_FILES) {
            //TODO add error messages
            return;
        }

        this.uploadedFiles.push(...files.slice(0, this.MAX_FILES - this.uploadedFiles.length));
    }

    browseFiles(fileInput: HTMLInputElement): void {
        fileInput.click();
    }
}
