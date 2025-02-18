import {BehaviorSubject, map, Observable, OperatorFunction, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environment/environment";

export abstract class AbstractCourseComponentsService<T extends { id: number | bigint }, C> {
    protected readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _subject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
    private _fetched: boolean = false

    protected constructor(private readonly _http: HttpClient) {}

    protected abstract get fetchAllValues(): Observable<any[]>;

    public get fetchAll(): Observable<T[]> {
        return this.fetchAllValues.pipe(this.translate, tap((response: T[]): void =>
        {
            this._subject.next(response)
            this._fetched = true;
        }));
    }

    protected abstract get translate(): OperatorFunction<any[], T[]>

    protected abstract createValue(createModels: C[]): Observable<any[]>;

    protected abstract deleteValue(id: number[]): Observable<void>;

    public create(models: C[]): Observable<T[]> {
        return this.createValue(models).pipe(
            tap((response: T[]): void => this._subject.next([...this.value, ...response]))
        );
    }

    public delete(id: (number | bigint)[]): Observable<void>
    {
        return this.deleteValue(id.map(((item: number | bigint): number => Number(item)))).pipe(map((): void => {
            this.value$.next(this.value.filter(((value: T): boolean =>!id.includes(Number(value.id)))));
        }));
    }

    public update(): void
    {
        this.value$.next([...this.value]);
    }

    public get value(): T[] {
        return this.value$.value;
    }

    public get value$(): BehaviorSubject<T[]> {
        if(!this.fetched)
        {
            this.fetchAll.subscribe();
        }
        return this._subject;
    }

    public get fetched(): boolean {
        return this._fetched;
    }

    protected get http(): HttpClient {
        return this._http;
    }
}
