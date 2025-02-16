import {AbstractSimpleCourseService} from "../user/courses/abstract-simple-course-service";
import {SelectionType} from "../common/abstract-list/abstract-list.component";

export abstract class AbstractSimpleList<T> {

    private _values: readonly T[] = [];

    protected readonly SelectionType: typeof SelectionType = SelectionType;

    protected constructor(private readonly _service: AbstractSimpleCourseService<T, any>)
    {
        this.subscribe();
    }

    protected subscribe(): void
    {
        this._service.value$.subscribe((value: T[]): void => { this._values = value; });
    }

    protected get values(): readonly T[] {
        return this._values;
    }

    protected set values(value: readonly T[]) {
        this._values = value;
    }

    protected get loaded(): boolean
    {
        return this.service.fetched;
    }

    protected get service(): AbstractSimpleCourseService<T, any> {
        return this._service;
    }

    protected abstract title(value: T): string;

    protected icon(value: T): string { return ''; }
    protected chips(value: T): string[] { return []; }
}
