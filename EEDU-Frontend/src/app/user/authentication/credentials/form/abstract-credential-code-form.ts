import {Component} from "@angular/core";
import {FormBuilder, Validators} from "@angular/forms";
import {AbstractCredentialForm} from "./abstract-credential-form";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialCodeForm extends AbstractCredentialForm<{ secret: string }>
{
    public constructor(formBuilder: FormBuilder)
    {
        super(formBuilder.group({
            secret: ['', Validators.required]
        }), 'secret');
    }

    protected override onSubmit()
    {
        const secret: string | undefined = this.secret;
        if (!secret)
        {
            this.errorSignal.set(this.emptyMessage);
            return;
        }
        this.emit({secret: secret});
    }

    protected abstract get emptyMessage(): string;

    protected get secret(): string | undefined
    {
        return this.form.get('secret')?.value;
    }
}
