import {Component, Input, signal, WritableSignal} from '@angular/core';
import {MatProgressBar} from "@angular/material/progress-bar";

@Component({
  selector: 'app-load',
  standalone: true,
    imports: [
        MatProgressBar,
    ],
  templateUrl: './load.component.html',
  styleUrl: './load.component.scss'
})
export class LoadComponent {
    @Input() errorSignal: WritableSignal<string> = signal('')
}
