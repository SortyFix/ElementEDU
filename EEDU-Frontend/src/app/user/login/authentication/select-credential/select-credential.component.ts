import {Component, Input} from '@angular/core';
import {MatList, MatListItem, MatListOption, MatSelectionList} from "@angular/material/list";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {CdkMenu, CdkMenuItem} from "@angular/cdk/menu";
import {MatDivider} from "@angular/material/divider";
import {CredentialMethod} from "../login-data/credential-method";

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
        MatDivider
    ],
  templateUrl: './select-credential.component.html',
  styleUrl: './select-credential.component.scss'
})
export class SelectCredentialComponent {
    @Input() loginName?: string;
    @Input() credentials?: CredentialMethod[];
}
