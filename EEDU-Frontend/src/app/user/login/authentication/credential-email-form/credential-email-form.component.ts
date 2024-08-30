import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {LoginData} from "../login-data/login-data";

@Component({
    selector: 'app-credential-email-form', standalone: true, imports: [
        FormsModule,
        MatButton,
        MatDialogClose,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule
    ], templateUrl: './credential-email-form.component.html', styleUrl: './credential-email-form.component.scss'
})
export class CredentialEmailFormComponent
{

    @Output() readonly submit = new EventEmitter<{ code: string }>();
    @Input() public _loginData?: LoginData;
    _code: string | undefined = undefined;

    get loginData(): LoginData
    {
        if(!this._loginData)
        {
            throw new Error(); // TODO enhance error handling
        }
        return this._loginData;
    }

    protected onSubmit()
    {
        if (!this._code)
        {
            return;
        }
        this.submit.emit({code: this._code});
    }
}
