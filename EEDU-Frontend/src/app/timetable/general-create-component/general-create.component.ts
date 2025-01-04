import {Component, input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-general-create',
  standalone: true,
    imports: [
        MatIcon,
        MatCardTitle,
        MatCardHeader,
        MatProgressBar,
        MatCard,
        MatIconButton,
        MatDialogClose,
        NgIf,
    ],
  templateUrl: './general-create.component.html',
  styleUrl: './general-create.component.scss'
})
export class GeneralCreateComponent {

    public readonly title: InputSignal<string> = input<string>('');
    public loading: boolean = false;

}
