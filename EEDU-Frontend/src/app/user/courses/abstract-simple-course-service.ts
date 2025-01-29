import {environment} from "../../../environment/environment";
import {BehaviorSubject, Observable, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";

export abstract class AbstractSimpleCourseService<T, C> {
    protected readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _subject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
    private _fetched: boolean = false

    protected constructor(private readonly _http: HttpClient) {}

    protected abstract get fetchAllValues(): Observable<T[]>;

    public get fetchAll(): Observable<T[]> {
        return this.fetchAllValues.pipe(tap((response: T[]): void =>
        {
            this._subject.next(response)
            this._fetched = true;
        }));
    }

    protected abstract createValue(createModels: C[]): Observable<T[]>;

    public create(models: C[]): Observable<T[]> {
        return this.createValue(models).pipe(tap((response: T[]): void => this._subject.next([...this.value, ...response])));
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
