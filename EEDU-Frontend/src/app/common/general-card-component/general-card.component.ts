import {Component, input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardHeader, MatCardSubtitle, MatCardTitle} from "@angular/material/card";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {NgIf} from "@angular/common";

@Component({
    selector: 'abstract-general-card',
    standalone: true,
    imports: [MatIcon, MatCardTitle, MatCardSubtitle, MatCardHeader, MatProgressBar, MatCard, MatIconButton, MatDialogClose, NgIf],
    templateUrl: './general-card.component.html',
    styleUrl: './general-card.component.scss'
})
export class GeneralCardComponent {

    public readonly title: InputSignal<string> = input<string>('');
    public readonly subtitle: InputSignal<string | null> = input<string | null>(null);
    public readonly loading: InputSignal<boolean> = input<boolean>(true);

}
