import {Component, Input} from '@angular/core';
import {MatDialogClose} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-default-dialog-actions',
  standalone: true,
    imports: [
        MatDialogClose,
        MatButton,
        NgIf
    ],
  templateUrl: './default-dialog-actions.component.html',
  styleUrl: './default-dialog-actions.component.scss'
})
export class DefaultDialogActionsComponent {

    @Input() submit = "Submit";
    @Input() cancel = "Cancel";

}
