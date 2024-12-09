import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogClose} from "@angular/material/dialog";
import {
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle
} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";

@Component({
    selector: 'app-event-dialog',
    standalone: true,
    imports: [
        MatCardContent,
        MatCard,
        MatCardHeader,
        MatIcon,
        MatDialogClose,
        MatIconButton,
        MatCardTitle,
        MatCardSubtitle,
        MatCardActions,
        MatButton
    ],
    templateUrl: './event-dialog.component.html',
    styleUrl: './event-dialog.component.scss'
})
export class EventDialogComponent {

    constructor(@Inject(MAT_DIALOG_DATA) public data: { description: string }) {}
}
