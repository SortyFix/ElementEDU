import { Component } from '@angular/core';
import {AbstractList} from "../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../management-loading-bar/management-loading-bar.component";
import {AbstractSimpleList} from "../abstract-simple-list";
import {SubjectModel} from "../../user/courses/subject/subject-model";
import {SubjectService} from "../../user/courses/subject/subject.service";

@Component({
  selector: 'app-subject-list',
    imports: [
        AbstractList,
        ManagementLoadingBar
    ],
  templateUrl: './file-contents.html',
})
export class SubjectListComponent extends AbstractSimpleList<SubjectModel> {
    public constructor(service: SubjectService) { super(service); }
    protected override icon(value: SubjectModel): string { return 'subject'; }
    protected override title(value: SubjectModel): string { return value.name; }
}
