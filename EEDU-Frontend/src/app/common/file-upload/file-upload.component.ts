import {Component, EventEmitter, inject, input, Input, InputSignal, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {NgIf} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
    standalone: true,
    selector: 'app-file-upload',
    imports: [
        MatIcon,
        NgIf
    ],
    templateUrl: './file-upload.component.html',
    styleUrl: './file-upload.component.scss'
})
export class FileUploadComponent {
    @Output() filesSelected: EventEmitter<FileList> = new EventEmitter<FileList>();
    public readonly maxFiles: InputSignal<number> = input<number>(5);
    public readonly accept: InputSignal<string> = input<string>("*/*");

    private _snackbar: MatSnackBar = inject(MatSnackBar);
    private _selectedFiles!: FileList;

    protected get selectedFiles(): FileList | null
    {
        return this._selectedFiles;
    }

    public onFileSelected(event$: Event): void {
        const input = event$.target as HTMLInputElement;
        if(input.files && input.files.length <= this.maxFiles()) {
            this._selectedFiles = input.files;
            this.filesSelected.emit(this._selectedFiles);
        }
        else
        {
            this._snackbar.open(`You can only upload ${this.maxFiles()} file(s) at a time.`, '', {
                duration: 3000
            });
        }
    }

    public get containsOneFile(): boolean {
        return this._selectedFiles && this._selectedFiles.length == 1;
    }

    public get containsMultipleFiles(): boolean {
        return this._selectedFiles && this._selectedFiles.length > 1;
    }

    public get singleFileName(): string {
        let firstItem = this._selectedFiles.item(0);

        if(firstItem)
        {
            return firstItem.name;
        }

        throw new Error("No file item found to get a file name from.")
    }
}
