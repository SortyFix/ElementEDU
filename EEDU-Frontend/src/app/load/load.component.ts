import {Component, Input, signal, WritableSignal} from '@angular/core';
import {MatProgressBar} from "@angular/material/progress-bar";
import {
    MatCard,
    MatCardContent,
    MatCardFooter,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle
} from "@angular/material/card";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {MatError} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-load',
  standalone: true,
    imports: [
        MatProgressBar,
        MatCard,
        MatCardHeader,
        MatCardContent,
        MatCardTitle,
        MatCardSubtitle,
        MatProgressSpinner,
        MatCardFooter,
        NgIf,
        MatError,
        NgOptimizedImage,
        MatIcon,
        MatButton,
        CdkCopyToClipboard,
        MatTooltip
    ],
  templateUrl: './load.component.html',
  styleUrl: './load.component.scss'
})
export class LoadComponent {
    @Input() errorSignal: WritableSignal<string> = signal('')
}
