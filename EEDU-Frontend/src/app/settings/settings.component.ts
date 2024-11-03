import {AfterViewInit, Component} from '@angular/core';
import {UserService} from "../user/user.service";
import {UserModel} from "../user/user-model";
import {UserListComponent} from "../user/user-list/user-list.component";

@Component({
  selector: 'app-settings',
  standalone: true,
    imports: [
        UserListComponent
    ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements AfterViewInit{

    private _userList: UserModel[] = [];

    constructor(private userService: UserService) {}

    ngAfterViewInit(): void {
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this._userList = users });
    }

    get userList(): UserModel[] {
        return this._userList;
    }
}
