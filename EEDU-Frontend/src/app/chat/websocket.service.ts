import {Injectable, OnInit} from '@angular/core';
import {Stomp} from "@stomp/stompjs";
import {AuthenticationService} from "../user/authentication/authentication.service";
import {UserService} from "../user/user.service";
import {HttpClient} from "@angular/common/http";
import {merge, Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class WebsocketService implements OnInit {
    private stompClient: any;

    constructor(protected userService: UserService, protected authenticationService: AuthenticationService, protected http: HttpClient) {
        this.connect();
    }

    ngOnInit(): void {
        // idk if i need this
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

    public listen<T>(topic: string): Observable<T> {
        return new Observable<T>(observer => {
            const subscription = this.stompClient.subscribe(`/topic/${topic}`,(message: any) => {
                console.log('Backend says: ', message.body);
                const parsedMessage: T = JSON.parse(message.body);
                observer.next(parsedMessage);
            });

            return () => {
                subscription.unsubscribe();
            }
        })
    }

    public multiListen<T>(topics: string[]): Observable<T> {
        const observables = topics.map(topic => this.listen<T>(topic));
        return merge(...observables);
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
