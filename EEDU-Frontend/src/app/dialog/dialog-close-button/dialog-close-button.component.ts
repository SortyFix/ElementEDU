import { Component } from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatDialogClose} from "@angular/material/dialog";
import {MatIconButton} from "@angular/material/button";

@Component({
  selector: 'app-dialog-close-button',
  standalone: true,
    imports: [
        MatIcon,
        MatDialogClose,
        MatIconButton
    ],
  templateUrl: './dialog-close-button.component.html',
  styleUrl: './dialog-close-button.component.scss'
})
export class DialogCloseButtonComponent {

}
