import {Component, input, InputSignal, OnInit} from '@angular/core';
import {GroupModel} from "../../group-model";
import {MatDialogContent} from "@angular/material/dialog";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {NgForOf, NgIf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {UserModel} from "../../user-model";
import {GroupService} from "../group.service";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";

@Component({
  selector: 'app-group-selection-list',
  standalone: true,
    imports: [
        MatDialogContent,
        MatProgressSpinner,
        NgForOf,
        MatCheckbox, NgIf, MatRadioGroup, MatRadioButton
    ],
  templateUrl: './group-selection-list.component.html',
  styleUrl: './group-selection-list.component.scss'
})
export class GroupSelectionList implements OnInit {

    user: InputSignal<UserModel | undefined> = input();
    private _loading: boolean = true;
    private _groups: GroupModel[] = [];
    private _selected: Set<string> = new Set();

    constructor(private _groupService: GroupService) {}

    public ngOnInit(): void {
        this._groupService.fetchAll().subscribe((groups: GroupModel[]): void => {
            this._groups = groups.filter((value: GroupModel): boolean => {
                return value.name !== 'student' && value.name !== 'teacher'
            });
            this._loading = false;
        });
    }

    protected get groups(): GroupModel[] {
        return this._groups;
    }

    protected isDisabled(group: GroupModel): boolean {
        const groupName: string = group.name;
        const user: UserModel | undefined = this.user();

        return !!user && user.loginName === 'root' && groupName === 'administrator';
    }

    protected isChecked(group: GroupModel): boolean {
        const user: UserModel | undefined = this.user();
        return !!user && user.inGroup(group.name) || this._selected.has(group.name) ;
    }

    protected isSelected(group: GroupModel): boolean {
        return this._selected.has(group.name);
    }

    protected toggle(group: GroupModel): void {
        if (this.isSelected(group)) {
            this._selected.delete(group.name);
            return;
        }

        this._selected.add(group.name);
    }

    protected get loading(): boolean {
        return this._loading;
    }

}
