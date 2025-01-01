import {Injectable} from '@angular/core';
import {Stomp} from "@stomp/stompjs";
import {AuthenticationService} from "../user/authentication/authentication.service";
import {UserService} from "../user/user.service";
import {HttpClient} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private stompClient: any;

    constructor(protected userService: UserService, protected authenticationService: AuthenticationService, protected http: HttpClient) {
        this.connect();
    }

    private connect() {
        this.authenticateWebsocket().subscribe(token => {
            const socket = new WebSocket(`ws://localhost:8080/ws-endpoint?token=${token}`);
            this.stompClient = Stomp.over(socket);
            this.stompClient.connect({}, (frame: any) => {
                this.listen('test');
                console.log(`Connected to websocket endpoint: ${frame}`);
                this.sendTestMessage();
            })
        })
    }

    public listen(topic: string) {
        return this.stompClient.subscribe(`/topic/${topic}`,(message: any) => {
            console.log('Backend says: ', message.body);
        });
    }

    private authenticateWebsocket() {
        return this.http.get<string>('http://localhost:8080/api/v1/chat/authenticate', {
            withCredentials: true,
            responseType: "text" as "json"
        });
    }

    public send(dest: string, message: string)
    {
        this.stompClient.send(dest, {
            withCredentials: true
        }, message);
    }

    public sendTestMessage() {
        this.send("/app/test", "This is a test!");
    }
}
