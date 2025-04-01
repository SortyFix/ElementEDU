import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {ChatService} from "../../chat/chat.service";
import {ChatModel} from "../../chat/models/chat-model";

@Component({
  selector: 'app-chat-card',
  standalone: true,
    imports: [
        NgForOf,
        NgIf
    ],
  templateUrl: './chat-card.component.html',
  styleUrl: './chat-card.component.scss'
})
export class ChatCardComponent implements OnInit {
    contactList: ChatModel[] = [];

    constructor(public chatService: ChatService) {
    }

    ngOnInit() {
        this.chatService.getAllChats().subscribe((models: ChatModel[]): void => {
            this.contactList = models.splice(0, 6);
        });
    }
}
