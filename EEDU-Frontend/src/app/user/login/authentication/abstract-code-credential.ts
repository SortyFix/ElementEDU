import {AbstractCredential} from "./abstract-credential";
import {Component} from "@angular/core";
import {FormBuilder, Validators} from "@angular/forms";

@Component({standalone: true, template: ''})
export abstract class AbstractCodeCredential<T> extends AbstractCredential<T>
{
    public constructor(formBuilder: FormBuilder)
    {
        super(formBuilder.group({
            code: ['', Validators.required]
        }));
    }

    protected get code(): string | undefined
    {
        return this.form.get('code')?.value;
    }
}
