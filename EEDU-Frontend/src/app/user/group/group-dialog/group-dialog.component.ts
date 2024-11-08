import {Component, OnInit} from '@angular/core';
import {GroupModel} from "../../group-model";

@Component({
  selector: 'app-group-dialog',
  standalone: true,
  imports: [],
  templateUrl: './group-dialog.component.html',
  styleUrl: './group-dialog.component.scss'
})
export class GroupDialogComponent implements OnInit {

    private _groups: GroupModel[] = [];

    ngOnInit() {

    }

}
