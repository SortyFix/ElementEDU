import {Component} from '@angular/core';
import {UserModel} from "../../user-model";
import {UserService} from "../../user.service";
import {AbstractList, ListItemInfo, SelectionType} from "../../../common/abstract-list/abstract-list.component";
import {AccountType} from "../../account-type";

@Component({
    selector: 'app-full-user-list',
    imports: [
        AbstractList,
        AbstractList
    ],
    templateUrl: './full-user-list.component.html',
    styleUrl: './full-user-list.component.scss'
})
export class FullUserListComponent {
    protected readonly SelectionType: typeof SelectionType = SelectionType;

    private readonly _users: UserModel[] = [];

    public constructor(userService: UserService) {
        userService.fetchAll.subscribe((users: UserModel[]): void => {
            this._users.length = 0;
            this._users.push(...users);
        })
    }

    public get values(): UserModel[] { return this._users; }

    protected get listData(): ListItemInfo<UserModel> {
        return {
            title: (value: UserModel): string => value.loginName,
            chips: (value: UserModel): string[] => [value.accountType, `${value.lastName}, ${value.firstName}`],
            icon: (value: UserModel): string => {
                switch (value.accountType) {
                    case AccountType.ADMINISTRATOR:
                        return 'manage_accounts';
                    case AccountType.TEACHER:
                        return 'how_to_reg';
                    default:
                        return 'person';
                }
            }
        }
    }
}
