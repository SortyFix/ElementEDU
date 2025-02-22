import {Directive, input, InputSignal} from "@angular/core";

@Directive()
export class ListItemContent<T>
{
    public readonly entry: InputSignal<T | null> = input<T | null>(null);
}
