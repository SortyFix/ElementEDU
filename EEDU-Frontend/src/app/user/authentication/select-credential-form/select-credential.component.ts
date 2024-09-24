import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {MatList, MatListItem, MatListOption, MatSelectionList} from "@angular/material/list";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {CdkMenu, CdkMenuItem} from "@angular/cdk/menu";
import {MatDivider} from "@angular/material/divider";
import {credentialDisplayName, CredentialMethod} from "../login-data/credential-method";
import {FormsModule} from "@angular/forms";
import {LoginData} from "../login-data/login-data";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-select-credential',
  standalone: true,
    imports: [
        MatList,
        MatListItem,
        MatSelectionList,
        MatListOption,
        MatButton,
        MatDialogClose,
        CdkMenu,
        CdkMenuItem,
        MatDivider,
        FormsModule,
        MatIcon
    ],
  templateUrl: './select-credential.component.html',
  styleUrl: './select-credential.component.scss'
})
export class SelectCredentialComponent {
    @Output() readonly submit = new EventEmitter<{ method: CredentialMethod } | boolean>();
    @Input() _loginData?: LoginData;
    @ViewChild('selectionList') selectionList!: MatSelectionList;

    protected readonly credentialDisplayName = credentialDisplayName;


    protected onSubmit()
    {
        const value: MatListOption = this.selectionList.selectedOptions.selected[0];
        if (!value)
        {
            return;
        }
        this.submit.emit({ method: value.value });
    }

    protected onCancel()
    {
        this.submit.emit(false);
    }

    protected get loginData(): LoginData
    {
        if(!this._loginData)
        {
            throw new Error("Login data was not set."); // TODO enhance error handling
        }
        return this._loginData;
    }
}
