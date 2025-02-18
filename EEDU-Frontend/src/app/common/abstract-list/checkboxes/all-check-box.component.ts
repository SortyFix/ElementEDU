import {Component, input, InputSignal} from '@angular/core';
import {MatCheckbox} from "@angular/material/checkbox";
import {NgIf} from "@angular/common";

@Component({
    selector: 'all-checkbox',
    template: `
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <mat-checkbox [checked]="checked()" [indeterminate]="indeterminate()" *ngIf="show()">
                Select All
            </mat-checkbox>
        </div>
    `,
    imports: [
        MatCheckbox,
        NgIf
    ]
})
export class AllCheckBoxComponent {
    public readonly checked: InputSignal<boolean> = input<boolean>(false);
    public readonly indeterminate: InputSignal<boolean> = input<boolean>(false);
    public readonly show: InputSignal<boolean> = input<boolean>(true);
}
