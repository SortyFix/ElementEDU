import {Component, EventEmitter, input, InputSignal, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatListItemTitle} from "@angular/material/list";
import {NgIf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
    selector: 'single-checkbox', template: `
        <a matListItemTitle>
            <mat-checkbox
                *ngIf="show()"
                [checked]="checked()"
                (click)="$event.stopPropagation()"
                (change)="onToggle.emit()"
                (keydown)="keyboardEvent.emit($event)">
                <a class="title">
                    <mat-icon class="icon" *ngIf="icon()">{{ icon() }}</mat-icon>
                    {{ title() }}
                </a>
            </mat-checkbox>
        </a>
    `, styles: '.title { user-select: none }', imports: [MatCheckbox, NgIf, MatListItemTitle, MatIcon]
})
export class SingleCheckBoxComponent {
    @Output() public readonly keyboardEvent: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;
    @Output() public readonly onToggle: EventEmitter<void> = new EventEmitter<void>;

    public readonly checked: InputSignal<boolean> = input<boolean>(false);
    public readonly icon: InputSignal<string | undefined> = input<string>();
    public readonly title: InputSignal<string> = input<string>('');
    public readonly show: InputSignal<boolean> = input<boolean>(true);
}
