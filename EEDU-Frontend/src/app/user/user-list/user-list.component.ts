import {Component, input, InputSignal} from '@angular/core';
import {UserModel, UserStatus} from "../user-model";
import {
    MatList,
    MatListItem,
    MatListItemTitle,
} from "@angular/material/list";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatChip, MatChipSet} from "@angular/material/chips";
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
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {MatLine} from "@angular/material/core";
import {AccessibilityService} from "../../accessibility.service";
import {UserService} from "../user.service";
import {MatDialog} from "@angular/material/dialog";
import {FilterMenuComponent} from "./filter-menu/filter-menu.component";
import {AccountType} from "../account-type";

@Component({
  selector: 'app-user-list',
  standalone: true,
    imports: [
        MatListItem,
        MatListItemTitle,
        MatIcon,
        MatChipSet,
        MatChip,
        MatExpansionPanel,
        MatAccordion,
        MatExpansionPanelTitle,
        MatExpansionPanelDescription,
        MatExpansionPanelHeader,
        MatCheckbox,
        MatFormField,
        MatInput,
        MatLabel,
        MatButton,
        NgIf,
        FormsModule,
        NgForOf,
        MatTooltip,
        MatList,
        MatLine,
    ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {

    public readonly userList: InputSignal<UserModel[]> = input([] as UserModel[]);
    protected filteredString: string = '';
    private _selected: Set<string> = new Set();
    private _editGroup: string | undefined = undefined;

    constructor(
        protected accessibilityService: AccessibilityService,
        private _userService: UserService, private _dialog: MatDialog
    ) {}

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
            const usersLength: number = this.users.length;
            return usersLength != 0 && this._selected.size === usersLength;
        }

        return this._selected.has(entry.loginName);
    }

    protected unselectAll(): void
    {
        this._selected.clear();
    }

    protected toggle(entry: UserModel | 'all'): void {

        if (entry === 'all') {
            if (this.isSelected('all')) {
                this.unselectAll();
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

    protected icon(user: UserModel): 'person' | 'how_to_reg' | 'manage_accounts'
    {
        switch (user.accountType)
        {
            case AccountType.ADMINISTRATOR:
                return 'manage_accounts';
            case AccountType.TEACHER:
                return 'how_to_reg';
            default:
                return 'person';
        }
    }

    protected tags(user: UserModel): string[]
    {
        return [user.accountType, `${user.lastName}, ${user.firstName}`];
    }

    protected status(user: UserModel): 'check_circle' | 'error' | 'receipt_long' | 'pending' {
        switch (user.status) {
            case UserStatus.PRESENT:
                return 'check_circle';
            case UserStatus.EXCUSED:
                return 'receipt_long';
            case UserStatus.UNEXCUSED:
                return 'error';
            case UserStatus.PROSPECTIVE:
                return 'pending';
        }
    }

    // todo implement lazy loading
    protected get users(): UserModel[] {
        const userList: UserModel[] = this.userList();

        if (!this.filteredString) {
            return this.sorted(userList);
        }

        const loweredFilter: string = this.filteredString.toLowerCase();

        return this.sorted(userList.filter((user: UserModel): boolean =>
            user.firstName.toLowerCase().includes(loweredFilter) ||
            user.lastName.toLowerCase().includes(loweredFilter) ||
            user.loginName.toLowerCase().includes(loweredFilter)
        ));
    }

    private sorted(users: UserModel[]): UserModel[] {
        return users.sort((a: UserModel, b: UserModel): number => a.lastName.localeCompare(b.lastName));
    }

    protected isEditingGroups(user: UserModel): boolean
    {
        return this._editGroup === user.loginName;
    }

    protected set editGroup(user: UserModel | undefined)
    {
        this._editGroup = user?.loginName;
    }

    public get getTheme() {
        return this._userService.getUserData.theme;
    }

    protected openFilterMenu(event: MouseEvent) {
        this._dialog.open(FilterMenuComponent, {
            position: {
                top: `${event.clientY}px`,
                left: `${event.clientX - 50}px`
            }
        });
    }
}
