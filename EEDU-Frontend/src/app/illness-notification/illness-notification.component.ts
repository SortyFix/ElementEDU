import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-illness-notification',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        MatFormField,
        MatDatepickerInput,
        MatDatepickerToggle,
        MatDatepicker,
        MatInput,
        MatLabel,
        MatIcon,
        MatHint,
        MatButton,
        MatSuffix,
        NgIf
    ],
  templateUrl: './illness-notification.component.html',
  styleUrl: './illness-notification.component.scss'
})
export class IllnessNotificationComponent {
    selectedFile: File | null = null;

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if(input.files && input.files.length > 0) {
            this.selectedFile = input.files[0];
        }
    }
}
