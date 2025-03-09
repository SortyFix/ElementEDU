import {Component} from '@angular/core';
import {AbstractSimpleCreateEntity} from "../../../entity/create-entity/abstract-simple-create-entity";
import {DialogRef} from "@angular/cdk/dialog";
import {PrivilegeService} from "../privilege.service";
import {
    SimpleCreateDialogComponent
} from "../../../entity/create-entity/s-create-dialog/simple-create-dialog.component";

@Component({
    imports: [SimpleCreateDialogComponent],
    template: '<app-simple-create-dialog [title]="title" (submit)="create($event)"></app-simple-create-dialog>',
})
export class CreatePrivilegesDialogComponent extends AbstractSimpleCreateEntity {

    public constructor(service: PrivilegeService, dialogRef: DialogRef) {
        super(service, dialogRef, "Create Privileges");
    }
}
