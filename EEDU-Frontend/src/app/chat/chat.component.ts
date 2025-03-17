import {AfterViewChecked, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {WebsocketService} from "./websocket.service";
import {MatButton, MatIconButton} from "@angular/material/button";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {HttpClient} from "@angular/common/http";
import {ChatCreationComponent} from "./chat-creation/chat-creation.component";
import {Dialog} from "@angular/cdk/dialog";
import {MatInput} from "@angular/material/input";
import {ChatModel} from "./models/chat-model";
import {FormsModule} from "@angular/forms";
import {UserService} from "../user/user.service";
import {MessageModel} from "./models/message-model";
import {Subscription} from "rxjs";
import {
    MatDrawer,
    MatDrawerContainer,
} from "@angular/material/sidenav";
import {environment} from "../../environment/environment";
import {ChatService} from "./chat.service";

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
        NgIf,
        MatFormField,
        FormsModule,
        MatIconButton,
        DatePipe,
        MatDrawerContainer,
        MatDrawer
    ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit, AfterViewChecked {

    @ViewChild('chatContainer') private chatContainer: ElementRef | undefined;
    chatList!: ChatModel[];
    currentChat!: number;
    currentSubscription!: Subscription;
    currentChatHistory!: MessageModel[];
    notificationList: bigint[] = [];
    messageContent!: string;

    constructor(public dialog: Dialog, public chatService: ChatService, public websocketService: WebsocketService, public http: HttpClient, public userService: UserService, private cdr: ChangeDetectorRef) {
    }

    public ngOnInit() {
        this.getAllChats();
        this.websocketService.onConnected().then(() => {
        })
    }

    public openDialog() {
        let dialogRef = this.dialog.open(ChatCreationComponent, {
            width: "80%",
            height: "80%"
        });
    }

    public getAllChats() {
        this.chatService.getAllChats().subscribe((models: ChatModel[]): void => {
            this.chatList = models;
            console.log(models);
        });
    }

    public sendMessage() {
        const json = {
            // @NotNull Long authorId, @NotNull Long chatId, @NotNull String body
            authorId: this.userService.getUserData.id,
            chatId: this.currentChat,
            body: this.messageContent
        }
        console.log(JSON.stringify(json));
        this.websocketService.send(`/app/send`, JSON.stringify(json));
        this.messageContent = "";
    }

    public openChat(chatId: bigint)
    {
        this.currentSubscription?.unsubscribe();
        this.currentChat = Number(chatId);
        this.getChat(this.currentChat);
        this.notificationList = this.notificationList.filter(id => id != chatId);
        this.currentSubscription = this.websocketService.listen<MessageModel>(`${chatId}`).subscribe(model => {
            this.currentChatHistory.push(model);
        });
    }

    public getChat(chatId: number) {
        this.chatService.getChat(chatId).subscribe(model => {
            this.currentChatHistory = model;
            console.log(this.currentChatHistory);
        });
    }

    public me(messageModel: MessageModel): boolean {
        return messageModel.authorId == this.userService.getUserData.id;
    }

    public getChatTitle(chatId: number)
    {
        console.log(this.chatList);
        return this.chatList.find(chat => chat.id == BigInt(chatId))?.chatTitle;
    }

    ngAfterViewChecked(): void {
        this.scrollToBottom();
    }

    public scrollToBottom(): void {
        if(this.chatContainer)
        {
            this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
        }
    }

    public isMobile(): boolean {
        return window.innerWidth <= 768;
    }

    protected readonly Number = Number;
}
