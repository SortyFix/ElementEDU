import {Component, input, InputSignal} from '@angular/core';
import {UserModel} from "../user-model";
import {MatListItem, MatListItemLine, MatListItemMeta, MatListItemTitle, MatNavList} from "@angular/material/list";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatChip, MatChipRow, MatChipSet} from "@angular/material/chips";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

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
        MatListItemMeta,
        MatExpansionPanel,
        MatAccordion,
        MatExpansionPanelTitle,
        MatExpansionPanelDescription,
        MatExpansionPanelHeader,
        MatCheckbox,
        MatListItemLine,
        MatFormField,
        MatInput,
        MatLabel
    ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {

    public readonly userList: InputSignal<UserModel[]> = input([] as UserModel[]);

    handleKeyDown(event: KeyboardEvent) {

        switch (event.key) { // fall through
            case 'Enter':

            case 'ArrowLeft':
            case 'ArrowRight':
            case "ArrowUp":
            case 'ArrowDown':
                event.stopPropagation();
                event.preventDefault();
        }
    }
}
