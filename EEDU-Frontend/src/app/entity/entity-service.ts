import {BehaviorSubject, finalize, map, Observable, OperatorFunction, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environment/environment";
import {AbstractSimpleCreateEntity} from "./create-entity/abstract-simple-create-entity";
import {ComponentType} from "@angular/cdk/overlay";

export interface DefaultRequiredPrivileges
{
    fetchPrivilege: string | null;
    createPrivilege: string | null;
    deletePrivilege: string | null;
}

export abstract class EntityService<P, T extends { id: P }, G, C> {

    private readonly _subject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);

    protected constructor(
        private readonly _http: HttpClient,
        private readonly _location: string,
        private readonly _defaultRequiredPrivileges: DefaultRequiredPrivileges,
        private readonly _createDialog: ComponentType<AbstractSimpleCreateEntity>
    ) {}


    private _fetched: boolean = false

    public get privileges(): DefaultRequiredPrivileges {
        return this._defaultRequiredPrivileges;
    }

    public get fetched(): boolean {
        return this._fetched;
    }

    public get createDialogType(): ComponentType<AbstractSimpleCreateEntity> {
        return this._createDialog;
    }

    public get translateValue(): OperatorFunction<G[], T[]> {
        return map((response: G[]): T[] => response.map((item: G): T => this.translate(item)));
    }

    public get value(): T[] {
        return this.value$.value;
    }

    public get value$(): BehaviorSubject<T[]> {
        if (!this.fetched) {
            this.fetchAll.subscribe();
        }
        return this._subject;
    }

    public get fetchAll(): Observable<T[]> {
        const url: string = `${this.BACKEND_URL}/get/all`
        return this.http.get<G[]>(url, {withCredentials: true}).pipe(this.translateValue, tap((response: T[]): void => {
            this._subject.next(this.sort(response));
            this._fetched = true;
        }), finalize((): void => {this._fetched = true}));
    }

    protected sort(input: T[]): T[]
    {
        return input.sort((o1: T, o2: T): 1 | -1 | 0 => {
            const id1: P = o1.id;
            const id2: P = o2.id;

            return (id1 > id2 ? 1 : id1 < id2 ? -1 : 0);
        });
    }

    protected get BACKEND_URL(): string {
        return `${environment.backendUrl}/${this._location}`;
    }

    protected get http(): HttpClient {
        return this._http;
    }

    public abstract translate(obj: G): T;

    public create(models: C[]): Observable<T[]> {
        const url: string = `${this.BACKEND_URL}/create`;
        return this.http.post<G[]>(url, this.toPackets(models), {withCredentials: true}).pipe(this.translateValue, tap((response: T[]): void => this.pushCreated(response)));
    }

    public fetch(id: P): Observable<T> {
        const url: string = `${this.BACKEND_URL}/get/${id}`;
        return this.http.get<G>(url).pipe(// this will be a part of my flashback of why I have gone crazy
            map((response: G): G[] => [response]), this.translateValue, map((response: any[]): any => response[0]));
    }

    public delete(id: P[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/delete/${id}`;
        return this.http.delete(url, {withCredentials: true}).pipe(map((): void => { this.postDelete(id); }));
    }

    public clearCache(): void {
        this._fetched = false;
    }

    public update(): void {
        this.value$.next([...this.value]);
    }

    protected toPackets(models: C[]): any[] {
        return models.map((model: C): any => {
            if (typeof model === 'object' && model !== null && 'toPacket' in model) {
                return model.toPacket;
            }

            return model;
        });
    }

    protected pushCreated(response: readonly T[]): void {
        this._subject.next([...this.value, ...response]);
    }

    protected postDelete(id: P[]): void {
        this.value$.next(this.value.filter(((value: T): boolean => !id.includes(value.id))));
    }
}
