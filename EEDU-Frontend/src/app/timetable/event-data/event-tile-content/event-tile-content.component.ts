import {Component, input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle} from "@angular/material/card";
import {NgIf} from "@angular/common";
import {UserService} from "../../../user/user.service";

@Component({
    selector: 'app-event-tile-content',
    standalone: true,
    imports: [
        MatIcon,
        MatIconButton,
        MatCardSubtitle,
        MatCardHeader,
        MatCard,
        MatCardContent,
        NgIf
    ],
    templateUrl: './event-tile-content.component.html',
    styleUrl: './event-tile-content.component.scss'
})
export class EventTileContentComponent {

    public readonly title: InputSignal<string> = input<string>('');

    constructor(private _userService: UserService) {}

    private _editing: boolean = false;

    public get editing(): boolean {
        return this._editing;
    }

    protected set editing(value: boolean) {
        this._editing = value;
    }

    protected get teacher(): boolean {
        return this._userService.getUserData.inGroup('teacher');
    }

    protected get icon(): 'check' | 'edit' {
        return this.editing ? 'check' : 'edit';
    }
}
