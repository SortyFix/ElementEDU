import { Component } from '@angular/core';
import {MatList, MatListItem, MatListOption, MatSelectionList} from "@angular/material/list";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
  selector: 'app-select-credential',
  standalone: true,
    imports: [
        MatList,
        MatListItem,
        MatSelectionList,
        MatListOption,
        MatButton,
        MatDialogClose
    ],
  templateUrl: './select-credential.component.html',
  styleUrl: './select-credential.component.scss'
})
export class SelectCredentialComponent {
    typesOfShoes: string[] = ['E-Mail', 'TOTP', 'SMS', 'Password'];
}
