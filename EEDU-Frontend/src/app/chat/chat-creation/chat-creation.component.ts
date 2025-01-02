import {Component, OnInit} from '@angular/core';
import {ReducedUserModel} from "../../user/reduced-user-model";
import {HttpClient} from "@angular/common/http";
import {MatListSubheaderCssMatStyler} from "@angular/material/list";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {NgForOf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {UserService} from "../../user/user.service";
import {ChatModel} from "../models/chat-model";

@Component({
  selector: 'app-chat-creation',
  standalone: true,
    imports: [
        MatListSubheaderCssMatStyler,
        MatFormField,
        MatInput,
        MatLabel,
        MatButton,
        MatDivider,
        MatIcon,
        NgForOf,
        FormsModule
    ],
  templateUrl: './chat-creation.component.html',
  styleUrl: './chat-creation.component.scss'
})
export class ChatCreationComponent {
    userList: ReducedUserModel[] = [];
    originalUserList: ReducedUserModel[] = [];
    searchQuery: string = '';

    constructor(private http: HttpClient, private userService: UserService) {
        this.getAllUsers();
    }

    private getAllUsers() {
        this.http.get<ReducedUserModel[]>("http://localhost:8080/api/v1/user/all/reduced", { withCredentials: true })
            .subscribe(list => {
                this.originalUserList = [...list];
                this.userList = list;
                this.filterUserList();
            });
    }

    public filterUserList(): void {
        const query = this.searchQuery.toLowerCase();

        if (query) {
            this.userList = this.originalUserList.filter(user =>
                (user.firstName + " " + user.lastName).toLowerCase().includes(query)
            );
        } else {
            this.userList = [...this.originalUserList];
        }
    }

    public createChat(userId: bigint) {
        let chatUsers = [this.userService.getUserData.id, userId]
        return this.http.post<ChatModel>("http://localhost:8080/api/v1/chat/create", chatUsers, {
            withCredentials: true
        }).subscribe(model => {
            console.log(model);
        });
    }
}

