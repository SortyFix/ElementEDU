import {Injectable} from '@angular/core';
import {Stomp} from "@stomp/stompjs";
import {AuthenticationService} from "../user/authentication/authentication.service";
import {UserService} from "../user/user.service";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private stompClient: any;
    private isConnected: Promise<void>;

    constructor(protected userService: UserService, protected authenticationService: AuthenticationService, protected http: HttpClient) {
        this.isConnected = new Promise((resolve) => {
            this.connect(() => resolve());
        });
    }

    private connect(onConnectCallback: () => void) {
        this.authenticateWebsocket().subscribe(token => {
            const socket = new WebSocket(`ws://localhost:8080/ws-endpoint?token=${token}`);
            this.stompClient = Stomp.over(socket);
            this.stompClient.connect({}, (frame: any): void => {
                this.listen('test');
                console.log(`Connected to websocket endpoint: ${frame}`);
                onConnectCallback();
                this.sendTestMessage();
            })
        })
    }

    public onConnected(): Promise<void> {
        return this.isConnected;
    }

    public listen<T extends object>(topic: string): Observable<T> {
        return new Observable<T>(observer => {
            const subscription: any = this.stompClient.subscribe(`/topic/${topic}`,(message: any) => {
                console.log('Backend says: ', message.body);
                const parsedMessage: T = JSON.parse(message.body);
                observer.next(parsedMessage);
            });

            return (): void => {
                subscription.unsubscribe();
            }
        })
    }


    private authenticateWebsocket(): Observable<string> {
        return this.http.get<string>('http://localhost:8080/api/v1/chat/authenticate', {
            withCredentials: true,
            responseType: "text" as "json"
        });
    }

    public send(dest: string, message: string): void
    {
        this.stompClient.send(dest, {
            withCredentials: true
        }, message);
    }

    public sendTestMessage(): void {
        this.send("/app/test", "This is a test!");
    }
}
