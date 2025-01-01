import {Component, OnInit} from '@angular/core';
import {WebsocketService} from "./websocket.service";
import {MatButton} from "@angular/material/button";
import {NgForOf} from "@angular/common";
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatLabel} from "@angular/material/form-field";
import {HttpClient} from "@angular/common/http";
import {ChatCreationComponent} from "./chat-creation/chat-creation.component";
import {Dialog} from "@angular/cdk/dialog";
import {MatInput} from "@angular/material/input";
import {ChatModel} from "./chat-model";

@Component({
  selector: 'app-chat',
  standalone: true,
    imports: [
        MatButton,
        NgForOf,
        MatDivider,
        MatIcon,
        MatLabel,
        MatInput,
        ChatCreationComponent
    ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {

    chatList!: ChatModel[];

    constructor(public dialog: Dialog, public websocketService: WebsocketService, public http: HttpClient) {
    }

    public ngOnInit() {
        this.getAllChats();
    }

    public openDialog() {
        let dialogRef = this.dialog.open(ChatCreationComponent, {
            width: "80%",
            height: "80%"
        });
    }

    public getAllChats() {
        return this.http.get<ChatModel[]>("http://localhost:8080/api/v1/chat/getChatList", {
            withCredentials: true
        }).subscribe(models => {
            this.chatList = models;
            console.log(models);
        });
    }
}
