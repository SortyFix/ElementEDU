import {Injectable} from '@angular/core';
import {environment} from '../../environment/environment';
import {Entity} from './entity';
import {Observable, of} from 'rxjs';
import {CreateModel} from './create-model';

@Injectable({
    providedIn: 'root'
})
export abstract class EntityService<P, C extends CreateModel, E extends Entity<P>> {

    protected constructor(
        private readonly _location: string
    ) {}

    protected get location(): string {
        return `${environment.backendUrl}/${this._location}`;
    }

    public get fetchAll(): Observable<readonly E[]>
    {
        return of([]); // TODO: Implement
    }

    public multiFetch(id: P[]): Observable<E[]>
    {
        return of([]); // TODO: Implement
    }

    public fetch(id: P): Observable<E>
    {
        return of(); // TODO: Implement
    }

    public create(model: C[]): Observable<E[]>
    {
        return of(); // TODO: Implement
    }

    public delete(id: P[]): Observable<void>
    {
        return of(); // TODO: Implement
    }
}
