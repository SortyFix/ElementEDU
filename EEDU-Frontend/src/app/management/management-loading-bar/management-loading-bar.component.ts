import {Component, input, InputSignal} from '@angular/core';
import {MatProgressBar} from "@angular/material/progress-bar";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-timetable-commons',
    imports: [
        MatProgressBar,
        NgIf
    ],
  templateUrl: './management-loading-bar.component.html',
  styleUrl: './management-loading-bar.component.scss'
})
export class ManagementLoadingBar { public readonly loading: InputSignal<boolean> = input<boolean>(true); }
