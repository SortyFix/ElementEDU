import {Component, EventEmitter, input, InputSignal, Output} from '@angular/core';
import {MatCheckbox} from "@angular/material/checkbox";
import {NgIf} from "@angular/common";

@Component({
    selector: 'all-checkbox', template: `
        <div style="display: flex;">
            <mat-checkbox
                [checked]="checked()"
                [indeterminate]="indeterminate()"
                *ngIf="show()"
                (change)="this.onToggle.emit()">
                Select All
            </mat-checkbox>
            <ng-content></ng-content>
        </div>
    `, imports: [MatCheckbox, NgIf]
})
export class AllCheckBoxComponent {
    @Output() public readonly onToggle: EventEmitter<void> = new EventEmitter<void>;

    public readonly checked: InputSignal<boolean> = input<boolean>(false);
    public readonly indeterminate: InputSignal<boolean> = input<boolean>(false);
    public readonly show: InputSignal<boolean> = input<boolean>(true);
}
