import {Component, OnInit} from '@angular/core';
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
import {NgForOf, NgIf} from "@angular/common";
import {HttpClient} from "@angular/common/http";
import {ReducedIllnessNotificationModel} from "./model/reduced-illness-notification-model";
import {Observable, Subscription} from "rxjs";
import {IllnessNotificationStatus} from "./illness-notification-status";
import {FileUploadComponent} from "../common/file-upload/file-upload.component";

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
        FormsModule,
        NgForOf,
        FileUploadComponent
    ],
  templateUrl: './illness-notification.component.html',
  styleUrl: './illness-notification.component.scss'
})
export class IllnessNotificationComponent implements OnInit {
    selectedFile: File | null = null;

    prefix: string = "http://localhost:8080/api/v1/illness/me";
    reason!: string;
    until!: Date | null;

    illnessNotifications!: ReducedIllnessNotificationModel[];

    constructor(private http: HttpClient) {}

    ngOnInit(): void {
        this.getOwnSickNotes().subscribe(list => {
            this.illnessNotifications = list.sort(model => Number(model.id));
            console.log(this.illnessNotifications);
        })
    }

    getOwnSickNotes(): Observable<ReducedIllnessNotificationModel[]> {
        return this.http.get<ReducedIllnessNotificationModel[]>(`${this.prefix}/my-notifications`, {
            withCredentials: true
        });
    }

    onFilesSelected(files: FileList): void {
        this.selectedFile = files.item(0);
    }

    getAppropriateIcon(status: IllnessNotificationStatus): 'check_circle' | 'schedule' | 'cancel' | 'dangerous'
    {
        switch (status.toLocaleString()) {
            case 'ACCEPTED':
                return 'check_circle';
            case 'PENDING':
                return 'schedule';
            case 'DECLINED':
                return 'cancel';
            default:
                return 'dangerous'
        }
    }

    formatDate(timestamp: bigint): string {
        const date = new Date(Number(timestamp) * 1000); // Convert UNIX timestamp (seconds) to milliseconds
        return date.toLocaleDateString("en-GB", { year: 'numeric', month: 'long', day: 'numeric' });
    }

    setDate(event: MatDatepickerInputEvent<Date>): void
    {
        this.until = event.value;
    }

    onRequestSent(): Subscription | undefined {
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
            console.log("No file selected.");
            return;
        }

        formData.append("file", this.selectedFile);

        return this.http.post(`${this.prefix}/excuse`, formData, {
            withCredentials: true
        }).subscribe(() => location.reload());
    }
}
