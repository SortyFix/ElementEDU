import {BehaviorSubject, map, Observable, OperatorFunction, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environment/environment";

export abstract class AbstractCourseComponentsService<P, T extends { id: P }, C> {

    protected readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _subject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);

    protected constructor(private readonly _http: HttpClient, private readonly _icon: string) {}

    private _fetched: boolean = false

    public get fetched(): boolean {
        return this._fetched;
    }

    public get icon(): string {
        return this._icon;
    }

    public get fetchAll(): Observable<T[]> {
        return this.fetchAllValues.pipe(this.translate, tap((response: T[]): void => {
            this._subject.next(response)
            this._fetched = true;
        }));
    }

    public abstract get translate(): OperatorFunction<any[], T[]>

    public get value(): T[] {
        return this.value$.value;
    }

    public get value$(): BehaviorSubject<T[]> {
        if (!this.fetched) {
            this.fetchAll.subscribe();
        }
        return this._subject;
    }

    protected abstract get fetchAllValues(): Observable<any[]>;

    protected get http(): HttpClient {
        return this._http;
    }

    public clearCache(): void {
        this._fetched = false;
    }

    public create(models: C[]): Observable<T[]> {
        return this.createValue(models).pipe(this.translate, tap((response: T[]): void => this.pushCreated(response)));
    }

    public delete(id: P[]): Observable<void> {
        return this.deleteValue(id).pipe(map((): void => { this.postDelete(id); }));
    }

    public update(): void {
        this.value$.next([...this.value]);
    }

    protected abstract createValue(createModels: C[]): Observable<any[]>;

    protected abstract deleteValue(id: P[]): Observable<void>;

    protected pushCreated(response: T[]): void {
        this._subject.next([...this.value, ...response]);
    }

    protected postDelete(id: P[]): void {
        this.value$.next(this.value.filter(((value: T): boolean => !id.includes(value.id))));
    }
}
