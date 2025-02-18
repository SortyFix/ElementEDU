import { Component } from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {SubjectModel} from "../subject-model";
import {SubjectService} from "../subject.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateSubjectComponent} from "../create-subject/create-subject.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {AbstractCourseComponentList} from "../../abstract-course-components/abstract-course-component-list";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";

@Component({
    selector: 'app-subject-list',
    imports: [
        MatProgressBar,
        AbstractList,
        MatIconButton,
        MatButton,
        MatIcon,
        NgIf,
    ],
    templateUrl: '../../abstract-course-components/abstract-course-components-list.html',
    styleUrl: '../../abstract-course-components/abstract-course-components-list.scss'
})
export class SubjectListComponent extends AbstractCourseComponentList<SubjectModel> {
    public constructor(service: SubjectService, dialog: MatDialog) { super(service, dialog, CreateSubjectComponent); }
    protected override icon(value: SubjectModel): string { return 'subject'; }
    protected override title(value: SubjectModel): string { return value.name; }
}
