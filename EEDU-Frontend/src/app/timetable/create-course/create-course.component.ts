import { Component } from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-create-course',
  standalone: true,
    imports: [
        MatCardContent,
        MatIcon,
        MatIconButton,
        MatDialogClose,
        MatCardTitle,
        MatCardHeader,
        NgIf,
        MatProgressBar,
        MatCard,
        MatLabel,
        MatFormField,
        MatInput
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
