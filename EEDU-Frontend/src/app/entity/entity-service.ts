import {Injectable} from '@angular/core';
import {environment} from '../../environment/environment';
import {Entity} from './entity';
import {BehaviorSubject, finalize, map, Observable, of, OperatorFunction, tap} from 'rxjs';
import {CreateModel} from './create-model';
import {HttpClient} from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export abstract class EntityService<P, C extends CreateModel, E extends Entity<P>> {

    private readonly CONTEXT: { withCredentials: true } = { withCredentials: true };

    private readonly _subject: BehaviorSubject<readonly E[]> = new BehaviorSubject<readonly E[]>([]);
    private _fetched: boolean = false;

    protected constructor(
        private readonly _location: string,
        private readonly _http: HttpClient
    ) {}

    protected get location(): string {
        return `${environment.backendUrl}/${this._location}`;
    }

    public get fetchAll(): Observable<readonly E[]>
    {
        return this._http.get<any>(`${this.location}/get`, this.CONTEXT).pipe(this.toEntitiesOperation);
    }

    public fetch(id: P): Observable<E>
    {
        return this._http.get<any>(`${this.location}/get/${id}`, this.CONTEXT).pipe(
            map((data: any): E => this.toEntity(data))
        );
    }

    public create(model: C[]): Observable<readonly E[]>
    {
        const packets: any[] = model.map((item: C): any => item.toPacket);
        return this._http.post<any>(`${this.location}/create`, packets, this.CONTEXT).pipe(
            this.toEntitiesOperation,
            tap((entities: readonly E[]): void => {this._subject.next(entities); /* TODO implement sorting */ }),
            finalize((): void => { this._fetched = true; })
        );
    }

    public delete(id: P[]): Observable<void>
    {
        return of(); // TODO: Implement
    }

    private get toEntitiesOperation(): OperatorFunction<any[], readonly E[]> {
        return map((data: any): readonly E[] => this.toEntities(data));
    }

    protected toEntities(data: any[]): readonly E[] {
        return data.map((item: any): E => this.toEntity(item));
    }

    protected abstract toEntity(data: any): E;

    public get fetched(): boolean {
        return this._fetched;
    }

    public get value(): readonly E[] {
        return this._subject.value;
    }

    public get value$(): Observable<readonly E[]> {
        if (!this.fetched) {
            this.fetchAll.subscribe();
        }
        return this._subject.asObservable();
    }

    protected pushCreated(response: readonly E[]): void {
        this._subject.next([...this.value, ...response]);
    }

    protected postDelete(id: readonly P[]): void {
        this._subject.next(this.value.filter(((value: E): boolean => { return false; /* TODO: Implement */ })));
    }
}
