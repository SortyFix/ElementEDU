import {Component, input, Input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {NgIf} from "@angular/common";

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

    public readonly maxFiles: InputSignal<number> = input<number>(5);
    private readonly _selectedFiles: File[] = [];

    protected get selectedFiles(): File[]
    {
        return this._selectedFiles;
    }

}
