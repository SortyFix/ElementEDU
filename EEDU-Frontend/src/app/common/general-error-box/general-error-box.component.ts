import {Component, input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {NgIf} from "@angular/common";

@Component({
    selector: 'general-error-box',
    imports: [MatIcon, NgIf],
    templateUrl: './general-error-box.component.html',
    styleUrl: './general-error-box.component.scss'
})
export class GeneralErrorBoxComponent {
    public readonly icon: InputSignal<string> = input<string>('warning');
    public readonly message: InputSignal<string> = input<string>('');
    public readonly subMessage: InputSignal<string | null> = input<string | null>(null);
}
