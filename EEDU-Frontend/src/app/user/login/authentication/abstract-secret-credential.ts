import {AbstractCredential} from "./abstract-credential";
import {Component} from "@angular/core";
import {FormBuilder, Validators} from "@angular/forms";

@Component({standalone: true, template: ''})
export abstract class AbstractSecretCredential extends AbstractCredential<{ secret: string }>
{
    public constructor(formBuilder: FormBuilder)
    {
        super(formBuilder.group({
            secret: ['', Validators.required]
        }));
    }

    protected override onSubmit()
    {
        console.log("NOW")
        const secret: string | undefined = this.secret;
        if (!secret)
        {
            this.errorSignal.set(this.emptyMessage());
            return;
        }
        this.emit({secret: secret});
    }

    protected abstract emptyMessage(): string;

    protected get secret(): string | undefined
    {
        return this.form.get('secret')?.value;
    }
}
