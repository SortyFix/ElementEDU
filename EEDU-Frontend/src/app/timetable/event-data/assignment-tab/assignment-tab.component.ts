import {Component, Input, input, InputSignal} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AppointmentService} from "../../../user/courses/appointment/appointment.service";
import {AppointmentEntryModel} from "../../../user/courses/appointment/entry/appointment-entry-model";
import {AccountType} from "../../../user/account-type";
import {UserService} from "../../../user/user.service";
import {AssignmentStudentViewComponent} from "./assignment-student-view/assignment-student-view.component";
import {AssignmentTeacherViewComponent} from "./assignment-teacher-view/assignment-teacher-view.component";

@Component({
    selector: 'app-assignment-tab',
    standalone: true,
    imports: [
        FormsModule,
        ReactiveFormsModule,
        NgIf,
        AssignmentStudentViewComponent,
        AssignmentTeacherViewComponent,
    ],
    templateUrl: './assignment-tab.component.html',
    styleUrl: './assignment-tab.component.scss'
})
export class AssignmentTabComponent {

    @Input() public appointment!: AppointmentEntryModel;
    public readonly editing: InputSignal<boolean> = input<boolean>(false);
    protected isDragging: boolean = false;
    protected readonly MAX_FILES: 5 = 5;
    protected readonly AccountType = AccountType;
    private _uploadedFiles: File[] = [];
    private readonly _accountType: AccountType;

    public constructor(private readonly _appointmentService: AppointmentService, userService: UserService) {
        this._accountType = userService.getUserData.accountType;
    }

    protected get accountType(): AccountType {
        return this._accountType;
    }
}
