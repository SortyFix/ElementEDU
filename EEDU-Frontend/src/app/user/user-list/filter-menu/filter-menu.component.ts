import { Component } from '@angular/core';
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
    private _students: boolean = true;
    private _administrator: boolean = false;


    protected set teacher(value: boolean) {
        this._teacher = value;
    }

    protected set students(value: boolean) {
        this._students = value;
    }

    protected set administrator(value: boolean) {
        this._administrator = value;
    }

    public get teacher(): boolean {
        return this._teacher;
    }

    public get students(): boolean {
        return this._students;
    }

    public get administrator(): boolean {
        return this._administrator;
    }
}
