import {Component, input, InputSignal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatChip, MatChipListbox} from "@angular/material/chips";
import {GeneralCardComponent} from "../general-card-component/general-card.component";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-delete-dialog',
    imports: [
        MatButton,
        MatCardActions,
        MatChipListbox,
        MatChip,
        MatCardContent,
        GeneralCardComponent
    ],
  templateUrl: './delete-dialog.component.html',
  styleUrl: './delete-dialog.component.scss'
})
export class DeleteDialogComponent {

    public readonly title: InputSignal<string> = input<string>('')
    public readonly entries: InputSignal<string[]> = input<string[]>([])
    public readonly loading: InputSignal<boolean> = input<boolean>(false)
    public readonly ref: InputSignal<MatDialogRef<any> | null> = input<MatDialogRef<any> | null>(null)

    protected onCancel(): void {
        this.ref()!.close(false);
    }

    protected onSubmit(): void {
        this.ref()!.close(true);
    }
}
