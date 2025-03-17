import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ChatModel} from "./models/chat-model";
import {environment} from "../../environment/environment";
import {Observable} from "rxjs";
import {MessageModel} from "./models/message-model";

@Injectable({
  providedIn: 'root'
})
export class ChatService {
    constructor(public http: HttpClient) { }

    public getAllChats(): Observable<ChatModel[]> {
        return this.http.get<ChatModel[]>(`${environment.backendUrl}/chat/getChatList`, {
            withCredentials: true
        });
    }

    public getChat(chatId: number): Observable<MessageModel[]> {
        return this.http.post<MessageModel[]>(`${environment.backendUrl}/chat/get/chat`, chatId, {
            withCredentials: true
        });
    }
}
