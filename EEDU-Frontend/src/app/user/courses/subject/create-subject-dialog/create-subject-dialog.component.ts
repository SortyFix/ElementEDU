import { Component } from '@angular/core';
import {AbstractSimpleCreateEntity} from "../../../../entity/create-entity/abstract-simple-create-entity";
import {DialogRef} from "@angular/cdk/dialog";
import {SubjectService} from "../subject.service";
import {SimpleCreateDialogComponent} from "../../../../entity/create-entity/simple-create-dialog/simple-create-dialog.component";

@Component({
  selector: 'app-create-subject-dialog',
    imports: [
        SimpleCreateDialogComponent
    ],
  template: '<app-simple-create-dialog [title]="title" (submit)="create($event)"></app-simple-create-dialog>',
})
export class CreateSubjectDialogComponent extends AbstractSimpleCreateEntity {

    public constructor(service: SubjectService, dialogRef: DialogRef) {
        super(service, dialogRef, 'Create Subjects');
    }
}
