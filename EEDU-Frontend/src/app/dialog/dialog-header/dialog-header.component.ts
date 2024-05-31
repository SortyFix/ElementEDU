import {Component, Input} from '@angular/core';
import {MatDialogTitle} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {DialogCloseButtonComponent} from "../dialog-close-button/dialog-close-button.component";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-dialog-header',
  standalone: true,
    imports: [
        MatDialogTitle,
        MatIcon,
        DialogCloseButtonComponent,
        NgIf
    ],
  templateUrl: './dialog-header.component.html',
  styleUrl: './dialog-header.component.scss'
})
export class DialogHeaderComponent {

    @Input() title?: string;
    @Input() icon?: string;
    @Input() closeButton= true;

}
