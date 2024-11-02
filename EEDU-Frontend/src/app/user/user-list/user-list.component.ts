import {Component, input, InputSignal} from '@angular/core';
import {UserModel} from "../user-model";
import {MatListItem, MatListItemMeta, MatListItemTitle, MatNavList} from "@angular/material/list";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatChip, MatChipRow, MatChipSet} from "@angular/material/chips";

@Component({
  selector: 'app-user-list',
  standalone: true,
    imports: [
        MatNavList,
        MatListItem,
        MatListItemTitle,
        MatIconButton,
        MatIcon,
        MatChipRow,
        MatChipSet,
        MatChip,
        MatListItemMeta
    ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {

    public readonly userList: InputSignal<UserModel[]> = input([] as UserModel[]);

}
