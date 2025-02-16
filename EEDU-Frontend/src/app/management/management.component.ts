import {Component, OnInit} from '@angular/core';
import {UserListComponent} from "../user/user-list/user-list.component";
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";

@Component({
    selector: 'app-management',
    imports: [
        UserListComponent
    ],
    templateUrl: './management.component.html',
    standalone: true,
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    userList: UserModel[] = [];

    constructor(protected userService: UserService) {
    }

    ngOnInit(): void {
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }
}
