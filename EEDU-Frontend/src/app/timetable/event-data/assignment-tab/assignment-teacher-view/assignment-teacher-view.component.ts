import {Component, input, InputSignal} from '@angular/core';

@Component({
    selector: 'app-assignment-teacher-view',
    standalone: true,
    imports: [],
    templateUrl: './assignment-teacher-view.component.html',
    styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent {

    public readonly editing: InputSignal<boolean> = input<boolean>(false);

}
