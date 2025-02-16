import { Component } from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../../../../management/management-loading-bar/management-loading-bar.component";
import {AbstractCourseComponentList} from "../../abstract-course-component-list";
import {SubjectModel} from "../subject-model";
import {SubjectService} from "../subject.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateSubjectComponent} from "../create-subject/create-subject.component";

@Component({
  selector: 'app-subject-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIcon
    ],
  templateUrl: '../../abstract-course-components-list.html',
})
export class SubjectListComponent extends AbstractCourseComponentList<SubjectModel> {
    public constructor(service: SubjectService, dialog: MatDialog) { super(service, dialog, CreateSubjectComponent); }
    protected override icon(value: SubjectModel): string { return 'subject'; }
    protected override title(value: SubjectModel): string { return value.name; }
}
