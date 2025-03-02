import {Component, EventEmitter, forwardRef, inject, input, InputSignal, Output, Type} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {NgClass, NgIf} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";

const type: Type<FileUploadComponent> = forwardRef((): typeof FileUploadComponent => FileUploadComponent);

@Component({
    standalone: true,
    selector: 'app-file-upload',
    imports: [
        MatIcon,
        NgIf,
        NgClass
    ],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: type, multi: true }
    ],
    templateUrl: './file-upload.component.html',
    styleUrl: './file-upload.component.scss'
})
export class FileUploadComponent implements ControlValueAccessor {
    @Output() filesSelected: EventEmitter<FileList> = new EventEmitter<FileList>();
    public readonly displayText: InputSignal<string> = input<string>("Add files to upload here")
    public readonly maxFiles: InputSignal<number> = input<number>(5);
    public readonly accept: InputSignal<string> = input<string>("*/*");

    private _snackbar: MatSnackBar = inject(MatSnackBar);
    private _selectedFiles: FileList | null = null;
    protected isDragging: boolean = false;

    private onChange: (files: FileList | null) => void = (): void => {};
    private onTouched: () => void = (): void => {};

    protected get selectedFiles(): FileList | null
    {
        return this._selectedFiles;
    }

    public onFileSelected(event$: Event): void {
        const input = event$.target as HTMLInputElement;
        this.addInputToBuffer(input);
    }

    onDrop(event: DragEvent): void {
        event.preventDefault();
        this.isDragging = false;
        this.addFileToBuffer(event.dataTransfer?.files);
    }

    public addInputToBuffer(input: HTMLInputElement) {
        if(input.files && input.files.length <= this.maxFiles()) {
            this._selectedFiles = input.files;
            this.filesSelected.emit(this._selectedFiles);
            this.onChange(this._selectedFiles);
            this.onTouched();
        }
        else
        {
            this._snackbar.open(`You can only upload ${this.maxFiles()} file(s) at a time.`, '', {
                duration: 3000
            });
        }
    }

    public addFileToBuffer(files: FileList | undefined): void
    {
        if(files && files.length <= this.maxFiles()) {
            this._selectedFiles = files;
            this.filesSelected.emit(this._selectedFiles);
            this.onChange(this._selectedFiles);
            this.onTouched();
        }
        else
        {
            this._snackbar.open(`You can only upload ${this.maxFiles()} file(s) at a time.`, '', {
                duration: 3000
            });
        }
    }

    public onDragEnter(event: DragEvent): void {
        event.preventDefault();
        this.isDragging = true;
    }

    public onDragOver(event: DragEvent): void {
        event.preventDefault();
    }

    public onDragLeave(event: DragEvent): void {
        event.preventDefault();
        this.isDragging = false;
    }

    public get selectedFileCount(): number {
        return this._selectedFiles?.length ?? 0;
    }

    public get containsOneFile(): boolean {
        return this.selectedFileCount === 1;
    }

    public get containsMultipleFiles(): boolean {
        return this.selectedFileCount > 1;
    }

    public get singleFileName(): string {
        let firstItem: File | null | undefined = this._selectedFiles?.item(0);

        if(firstItem)
        {
            return firstItem.name;
        }

        throw new Error("No file item found to get a file name from.")
    }

    public writeValue(files: FileList | null): void {
        this._selectedFiles = files;
    }

    public registerOnChange(func: (files: FileList | null) => void): void {
        this.onChange = func;
    }

    public registerOnTouched(func: () => void): void {
        this.onTouched = func;
    }
}
