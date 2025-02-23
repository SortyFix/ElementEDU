import {Component, input, InputSignal} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
    selector: 'app-assignment-teacher-view',
    standalone: true,
    imports: [
        NgIf
    ],
    templateUrl: './assignment-teacher-view.component.html',
    styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent {

    public readonly editing: InputSignal<boolean> = input<boolean>(false);

}
