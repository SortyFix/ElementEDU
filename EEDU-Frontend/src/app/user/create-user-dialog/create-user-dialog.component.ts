import { Component } from '@angular/core';
import {AbstractCreateEntity} from "../../entity/create-entity/abstract-create-entity";
import {DialogRef} from "@angular/cdk/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from "../user.service";
import {UserCreateModel} from "../user-create-model";
import {GroupService} from "../group/group.service";
import {ThemeService} from "../../theming/theme.service";
import {ThemeModel} from "../../theming/theme-model";
import {GroupModel} from "../group/group-model";
import {AccountType} from "../account-type";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {GeneralCardComponent} from "../../common/general-card-component/general-card.component";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatCheckbox} from "@angular/material/checkbox";
import {SelectionInput} from "../../common/selection-input/selection-input.component";
import {NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
    imports: [
        MatLabel,
        ReactiveFormsModule,
        MatInput,
        MatFormField,
        MatCardContent,
        GeneralCardComponent,
        MatSelect,
        MatOption,
        MatCheckbox,
        SelectionInput,
        NgIf,
        MatCardActions,
        MatButton,
        MatDialogClose
    ],
  templateUrl: './create-user-dialog.component.html',
  styleUrl: './create-user-dialog.component.scss'
})
export class CreateUserDialogComponent extends AbstractCreateEntity {

    private _themes: readonly ThemeModel[] = [];
    private _groups: readonly GroupModel[] = [];
    private _loginNameEdited: boolean = false;


    protected get loginNameEdited(): boolean {
        return this._loginNameEdited;
    }

    protected onLoginNameEdited(): void
    {
        this._loginNameEdited = true;
    }

    public constructor(service: UserService, dialogRef: DialogRef, formBuilder: FormBuilder,
                       private readonly _themeService: ThemeService, private readonly _groupService: GroupService) {
        super(service, dialogRef, formBuilder, "Create User");
        this._groupService.value$.subscribe((groups: GroupModel[]): void => { this._groups = groups; })
    }


    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            firstName: [null, Validators.required],
            lastName: [null, Validators.required],
            loginName: [null, Validators.required],
            accountType: [null, Validators.required],
            enabled: [true, Validators.required],
            theme: [{ id: 1 }],
            groups: [[]]
        });
    }

    protected get accountTypes(): AccountType[]
    {
        return Object.values(AccountType);
    }

    protected override get createModel(): any[] {
        return [UserCreateModel.fromObject(this.form.value)];
    }

    protected get groups(): readonly GroupModel[] {
        return this._groups.filter((group: GroupModel): boolean =>
        {
            switch (group.id) {
                case "administrator":
                case "teacher":
                case "student":
                    return false;
                default:
                    return true;
            }
        });
    }

    protected updateLoginName(): void {
        if(this._loginNameEdited)
        {
            return;
        }

        const firstName: string | null = this.form.get('firstName')?.value;
        const lastName: string | null = this.form.get('lastName')?.value;

        this.form.get('loginName')?.setValue(`${firstName}.${lastName || ''}`);
    }
}
