import {AbstractCourseComponentsCreate} from "./abstract-course-components-create";
import {MatChipInputEvent} from "@angular/material/chips";
import {signal, WritableSignal} from "@angular/core";

export class AbstractCourseComponentsCreateMultiple<T> extends AbstractCourseComponentsCreate<T>
{
    private readonly _chips: WritableSignal<string[]> = signal([]);

    protected override get createModel(): any[] {
        return (
            this.form.get('name')?.value as string[]).map((item: string): { name: string } => {return { name : item };}
        )
    }

    protected get chips(): WritableSignal<string[]> { return this._chips; }

    protected override get canSubmit(): boolean { return this._chips().length > 0; }

    // inspired by https://material.angular.io/components/chips/overview#chips-reactive-form

    protected add(event: MatChipInputEvent): void
    {
        const value: string = (event.value || '').trim();

        if (value)
        {
            this.chips.update((keywords: string[]): string[] => [...keywords, value]);
        }

        event.chipInput!.clear();
    }

    protected remove(keyword: string): void
    {
        this.chips.update((keywords: string[]): string[] => {
            const index: number = keywords.indexOf(keyword);
            if (index < 0)
            {
                return keywords;
            }

            keywords.splice(index, 1);
            return [...keywords];
        });
    }
}
