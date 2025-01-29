import {Component, input, InputSignal} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AssignmentModel} from "../../../user/courses/appointment/entry/assignment-model";
import {MatIcon} from "@angular/material/icon";
import {UserService} from "../../../user/user.service";

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
        MatIcon,
    ],
  templateUrl: './assignment-tab.component.html',
  styleUrl: './assignment-tab.component.scss'
})
export class AssignmentTabComponent {

    public readonly editing: InputSignal<boolean> = input<boolean>(false);
    public readonly assignment: InputSignal<AssignmentModel | undefined> = input<AssignmentModel | undefined>();
    protected isDragging: boolean = false;
    protected uploadedFiles: File[] = [];

    private readonly MAX_FILES: 3 = 3;

    constructor(private _userService: UserService) {}

    protected get isTeacher(): boolean {
        return this._userService.getUserData.inGroup('teacher');
    }

    protected get isStudent(): boolean {
        return this._userService.getUserData.inGroup('student');
    }

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
