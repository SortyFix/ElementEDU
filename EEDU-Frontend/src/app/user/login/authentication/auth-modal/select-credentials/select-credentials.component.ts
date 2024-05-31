import {Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {DialogHeaderComponent} from "../../../../../dialog/dialog-header/dialog-header.component";
import {MatDialogActions, MatDialogClose, MatDialogContent} from "@angular/material/dialog";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatListItem, MatListItemIcon, MatListOption, MatNavList, MatSelectionList} from "@angular/material/list";
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatLine, MatOption} from "@angular/material/core";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatSelect} from "@angular/material/select";
import {MatButton} from "@angular/material/button";
import {NgForOf} from "@angular/common";
import {
    DefaultDialogActionsComponent
} from "../../../../../dialog/default-dialog-actions/default-dialog-actions.component";

@Component({
  selector: 'app-select-credentials',
  standalone: true,
    imports: [
        DialogHeaderComponent,
        MatDialogContent,
        MatSelectionList,
        MatListOption,
        NgForOf,
        MatIcon,
        MatDialogActions,
        MatButton,
        MatDialogClose,
        DefaultDialogActionsComponent,
        FormsModule
    ],
  templateUrl: './select-credentials.component.html',
  styleUrl: './select-credentials.component.scss'
})
export class SelectCredentialsComponent {

    @ViewChild("credentialSelection") credentialSelection?: MatSelectionList;
    credentials: { icon: string, text: string }[] = [{icon: "timer", text: 'TOTP'}, {icon: "sms", text: 'SMS'}, {icon: "mail", text: 'EMAIL'}];

    onSubmit(value: string)
    {
        if(!value)
        {
            return;
        }
        console.log(value)
    }

}
