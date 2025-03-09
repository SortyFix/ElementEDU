import {Component, EventEmitter, input, InputSignal, Output, signal, WritableSignal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {GeneralCardComponent} from "../../../common/general-card-component/general-card.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatChipGrid, MatChipInput, MatChipInputEvent, MatChipRemove, MatChipRow} from "@angular/material/chips";
import {MatIcon} from "@angular/material/icon";
import {MatDialogClose} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
    selector: 'app-simple-create-dialog',
    imports: [ReactiveFormsModule, GeneralCardComponent, MatCardContent, MatLabel, MatSuffix, MatFormField, MatChipGrid, MatChipRow, MatChipRemove, MatIcon, MatCardActions, MatDialogClose, MatButton, MatInput, MatChipInput, MatTooltip],
    templateUrl: './simple-create-dialog.component.html',
    styleUrl: './simple-create-dialog.component.scss'
})
export class SimpleCreateDialogComponent {

    @Output() public submit: EventEmitter<{ id: string }[]> = new EventEmitter<{ id: string }[]>();
    public readonly title: InputSignal<string> = input<string>('');

    private readonly _chips: WritableSignal<string[]> = signal<string[]>([]);
    private readonly _form: FormGroup;

    public constructor(formBuilder: FormBuilder) {
        this._form = formBuilder.group({id: [null, Validators.required]})
    }

    protected get createModel(): any[] {
        return (this.form.get('id')?.value as string[]).map((item: string): { id: string } => { return {id: item};})
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected get chips(): WritableSignal<string[]> {
        return this._chips;
    }

    protected onSubmit(event: SubmitEvent): void {
        // prevent default submit event
        event.stopImmediatePropagation();
        event.stopPropagation();

        if (this.form.invalid) {
            return;
        }

        this.submit.emit(this.createModel);
    }

    // inspired by https://material.angular.io/components/chips/overview#chips-reactive-form

    protected add(event: MatChipInputEvent): void {

        const value: string = (event.value || '').trim();

        if (value) {
            this.chips.update((keywords: string[]): string[] => {
                // deduplicate
                const filteredKeywords: string[] = keywords.filter((keyword: string): boolean => keyword !== value);
                return [...filteredKeywords, value];
            });
        }

        event.chipInput!.clear();
    }

    protected remove(keyword: string): void {
        this.chips.update((keywords: string[]): string[] => {
            const index: number = keywords.indexOf(keyword);
            if (index < 0) {
                return keywords;
            }

            keywords.splice(index, 1);
            return [...keywords];
        });
    }
}
