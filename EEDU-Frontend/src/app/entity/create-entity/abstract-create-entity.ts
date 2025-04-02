import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {DialogRef} from "@angular/cdk/dialog";
import {EntityService} from "../entity-service";
import {AbstractSimpleCreateEntity} from "./abstract-simple-create-entity";

export abstract class AbstractCreateEntity extends AbstractSimpleCreateEntity {

    private readonly _form: FormGroup;

    protected constructor(service: EntityService<any, any, any, any>, dialogRef: DialogRef, formBuilder: FormBuilder, title: string) {
        super(service, dialogRef, title)
        this._form = this.getForm(formBuilder);
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected get loading(): boolean {
        return false;
    }

    protected get createModel(): any[] {
        return [this.form.value];
    }

    protected get canSubmit(): boolean {
        return this.form.valid;
    }

    protected getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({id: [null, Validators.required]});
    }

    protected onSubmit(): void {
        if (!this.canSubmit) {
            return;
        }

        this.create(this.createModel);
    }
}
