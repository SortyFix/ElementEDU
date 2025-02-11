import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerInputEvent,
    MatDatepickerToggle
} from "@angular/material/datepicker";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {NgIf} from "@angular/common";
import {HttpClient} from "@angular/common/http";
import {FileService} from "../file/file.service";

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
        NgIf,
        FormsModule
    ],
  templateUrl: './illness-notification.component.html',
  styleUrl: './illness-notification.component.scss'
})
export class IllnessNotificationComponent {
    selectedFile: File | null = null;

    prefix: string = "http://localhost:8080/api/v1/illness/me";
    reason!: string;
    until!: Date | null;

    constructor(private http: HttpClient) {}

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if(input.files && input.files.length > 0) {
            this.selectedFile = input.files[0];
        }
    }

    setDate(event: MatDatepickerInputEvent<Date>)
    {
        this.until = event.value;
    }

    onRequestSent() {
        console.log(this.until);
        if (!this.until)
        {
            console.error("Not everything filled out.")
            return;
        }

        let unixTimestamp = Math.floor(this.until?.getTime() / 1000);
        let formData: FormData = new FormData();

        formData.append("reason", this.reason);
        formData.append("expirationTime", unixTimestamp.toString());

        if(!this.selectedFile)
        {
            return;
        }

        formData.append("file", this.selectedFile);

        return this.http.post(`${this.prefix}/excuse`, formData, {
            withCredentials: true
        }).subscribe(() => location.reload());
    }
}
