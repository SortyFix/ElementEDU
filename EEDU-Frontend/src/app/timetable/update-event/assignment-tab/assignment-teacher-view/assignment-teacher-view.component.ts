import {AfterViewInit, Component, input, InputSignal} from '@angular/core';
import {GeneralSelectionInput} from "../../../general-selection-input/general-selection-input.component";
import {CourseModel} from "../../../../user/courses/course-model";
import {CourseService} from "../../../../user/courses/course.service";
import {UserModel} from "../../../../user/user-model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-assignment-teacher-view',
  standalone: true,
    imports: [
        GeneralSelectionInput,
        NgIf
    ],
  templateUrl: './assignment-teacher-view.component.html',
  styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent implements AfterViewInit {

    public readonly course: InputSignal<CourseModel | undefined> = input<CourseModel | undefined>(undefined);
    private readonly _users: UserModel[] = [];
    constructor(private _courseService: CourseService) {}

    public ngAfterViewInit() {

        const courseModel: CourseModel | undefined = this.course();
        if(!courseModel)
        {
            return;
        }

        this._courseService.fetchUsers(courseModel.id).subscribe((users: UserModel[]) => {this._users.push(...users);});
    }


    protected get users(): UserModel[] {
        return this._users;
    }
}
