import {Component, input, InputSignal} from '@angular/core';
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-footer-buttons',
  standalone: true,
    imports: [
        MatButton
    ],
  templateUrl: './footer-buttons.component.html',
  styleUrl: './footer-buttons.component.scss'
})
export class FooterButtonsComponent {

    public readonly buttonText: InputSignal<string> = input<string>('Submit');
}
