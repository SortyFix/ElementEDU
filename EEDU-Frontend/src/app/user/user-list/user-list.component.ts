import {Component, input, InputSignal} from '@angular/core';
import {UserModel} from "../user-model";
import {MatListItem, MatListItemLine, MatListItemMeta, MatListItemTitle, MatNavList} from "@angular/material/list";
import {MatButton, MatIconButton} from "@angular/material/button";
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
import {AccessibilityService} from "../../accessibility.service";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

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
        MatLabel,
        MatButton,
        NgIf,
        FormsModule,
        NgForOf
    ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {

    protected filteredString: string = '';
    public readonly userList: InputSignal<UserModel[]> = input([] as UserModel[]);
    private _selected: Set<string> = new Set();

    constructor(protected accessibilityService: AccessibilityService) {}

    handleKeyDown(event: KeyboardEvent, user: UserModel) {
        // noinspection FallThroughInSwitchStatementJS
        switch (event.key) { // fall through
            // @ts-ignore
            case ' ':
                this.toggle(user)
            case 'ArrowLeft':
            case 'ArrowRight':
            case "ArrowUp":
            case 'ArrowDown':
                event.stopPropagation();
                event.preventDefault();
        }
    }

    protected get partiallySelected(): boolean {
        return this._selected.size > 0 && !this.isSelected('all');
    }

    protected isSelected(entry: UserModel | 'all'): boolean {
        if (entry === 'all') {
            return this._selected.size === this.users.length;
        }

        return this._selected.has(entry.loginName);
    }

    protected toggle(entry: UserModel | 'all'): void {

        if (entry === 'all') {
            if (this.isSelected('all')) {
                this._selected.clear();
                return;
            }

            this.users.forEach((userModel: UserModel): Set<string> => this._selected.add(userModel.loginName));
            return;
        }

        // delete if selected
        if (this.isSelected(entry)) {
            this._selected.delete(entry.loginName);
            return;
        }

        this._selected.add(entry.loginName);
    }

    protected get users(): UserModel[] {
        return this.userList();
    }
}
