import {Component} from '@angular/core';
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
    selector: 'app-filter-menu',
    imports: [
        MatCheckbox
    ],
    templateUrl: './filter-menu.component.html',
    styleUrl: './filter-menu.component.scss'
})
export class FilterMenuComponent {

    private _teacher: boolean = true;

    public get teacher(): boolean {
        return this._teacher;
    }

    protected set teacher(value: boolean) {
        this._teacher = value;
    }

    private _students: boolean = true;

    public get students(): boolean {
        return this._students;
    }

    protected set students(value: boolean) {
        this._students = value;
    }

    private _administrator: boolean = false;

    public get administrator(): boolean {
        return this._administrator;
    }

    protected set administrator(value: boolean) {
        this._administrator = value;
    }
}
