import {Component, Inject, OnInit} from '@angular/core';
import {MatCheckbox} from "@angular/material/checkbox";
import {NgForOf, NgIf} from "@angular/common";
import {
    MAT_DIALOG_DATA,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {UserModel} from "../../user-model";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {GroupSelectionList} from "../group-list/group-selection-list.component";

@Component({
  selector: 'app-group-dialog',
  standalone: true,
    imports: [
        MatCheckbox,
        NgForOf,
        MatDialogContent,
        MatDialogActions,
        MatButton,
        MatDialogClose,
        MatDialogTitle,
        MatProgressSpinner,
        NgIf,
        GroupSelectionList
    ],
  templateUrl: './group-dialog.component.html',
  styleUrl: './group-dialog.component.scss'
})
export class GroupDialogComponent {

    constructor(@Inject(MAT_DIALOG_DATA) public user: UserModel | undefined) {}
}
