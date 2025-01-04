import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {GeneralSelectionInput} from "../create-appointment/general-selection-input/general-selection-input.component";
import {GeneralCreateComponent} from "../general-create-component/general-create.component";

@Component({
  selector: 'app-create-course',
  standalone: true,
    imports: [
        MatCardContent,
        MatDialogClose,
        MatLabel,
        MatFormField,
        MatInput,
        GeneralSelectionInput,
        GeneralCreateComponent,
        MatCardActions,
        MatButton
    ],
  templateUrl: './create-course.component.html',
  styleUrl: './create-course.component.scss'
})
export class CreateCourseComponent {

    private _loading: boolean = false;

    protected get loading(): boolean {
        return this._loading;
    }
}
