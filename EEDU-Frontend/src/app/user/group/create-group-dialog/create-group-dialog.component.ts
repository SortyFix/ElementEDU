import {Component} from '@angular/core';
import {AbstractCreateEntity} from "../../../entity/create-entity/abstract-create-entity";
import {DialogRef} from "@angular/cdk/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {GroupService} from "../group.service";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {GeneralCardComponent} from "../../../common/general-card-component/general-card.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {SelectionInput} from "../../../common/selection-input/selection-input.component";
import {PrivilegeModel} from "../privilege/privilege-model";
import {PrivilegeService} from "../privilege/privilege.service";
import {NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
    imports: [MatCardContent, GeneralCardComponent, ReactiveFormsModule, MatLabel, MatFormField, MatInput, SelectionInput, NgIf, MatCardActions, MatButton, MatDialogClose],
    templateUrl: './create-group-dialog.component.html',
    styleUrl: './create-group-dialog.component.scss'
})
export class CreateGroupDialogComponent extends AbstractCreateEntity {

    public constructor(service: GroupService, dialogRef: DialogRef, formBuilder: FormBuilder, private readonly _privilegeService: PrivilegeService) {
        super(service, dialogRef, formBuilder, "Create Group");
        this._privilegeService.value$.subscribe((value: PrivilegeModel[]): void => {
            this._privileges = value;
        });
    }

    private _privileges: readonly PrivilegeModel[] = [];

    protected get privileges(): readonly PrivilegeModel[] {
        return this._privileges;
    }

    protected override get createModel(): any[] {
        return [this.form.value];
    }

    protected override get loading(): boolean {
        return !this._privilegeService.fetched;
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            id: [null, Validators.required],
            privileges: [[]]
        });
    }
}
