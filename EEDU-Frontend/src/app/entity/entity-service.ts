import {Injectable} from '@angular/core';
import {environment} from '../../environment/environment';
import {Entity} from './entity';

@Injectable({
    providedIn: 'root'
})
export abstract class EntityService<P, E extends Entity<P>> {

    protected constructor(
        private readonly _location: string
    ) {}

    protected get location(): string {
        return `${environment.backendUrl}/api/v1/${this._location}`;
    }
}
