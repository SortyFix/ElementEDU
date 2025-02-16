import {Component} from '@angular/core';
import {UserModel} from "../../user-model";
import {UserService} from "../../user.service";
import {
    AbstractList, SelectionType
} from "../../../common/abstract-list/abstract-list.component";
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
export class FullUserListComponent
{
    protected readonly SelectionType: typeof SelectionType = SelectionType;

    private readonly _users: UserModel[] = [];

    public constructor(userService: UserService)
    {
        userService.fetchAll.subscribe((users: UserModel[]): void =>
        {
            this._users.length = 0;
            this._users.push(...users);
        })
    }

    public get chips(): ((value: UserModel) => string[]) {
        return (value: UserModel): string[] =>
        {
            return [value.accountType, `${value.lastName}, ${value.firstName}`];
        }
    }

    public get icon(): ((value: UserModel) => string) {
        return (value: UserModel): string => {
            switch (value.accountType)
            {
                case AccountType.ADMINISTRATOR: return 'manage_accounts';
                case AccountType.TEACHER: return 'how_to_reg';
                default: return 'person';
            }
        };
    }

    public get values(): UserModel[] { return this._users; }

    public title(value: UserModel): string { return value.loginName; }
}
