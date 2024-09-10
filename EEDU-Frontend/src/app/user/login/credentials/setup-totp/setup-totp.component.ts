import {Component, Input} from '@angular/core';
import {LoginData} from "../../authentication/login-data/login-data";
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatProgressBar} from "@angular/material/progress-bar";

@Component({
  selector: 'app-setup-totp',
  standalone: true,
    imports: [
        MatFormField,
        MatInput,
        ReactiveFormsModule,
        MatButton,
        MatDialogClose,
        MatProgressBar
    ],
  templateUrl: './setup-totp.component.html',
  styleUrl: './setup-totp.component.scss'
})
export class SetupTOTPComponent {

    @Input() private _loginData?: LoginData;
    private _loading: boolean = true;

    protected get loading(): boolean
    {
        return this._loading;
    }

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }
}
