import {Component, ViewChild} from '@angular/core';
import {MatListOption, MatSelectionList} from "@angular/material/list";
import {MatButton} from "@angular/material/button";
import {credentialDisplayName} from "../login-data/credential-method";
import {FormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {AuthenticationService} from "../authentication.service";
import {LoginData} from "../login-data/login-data";
import {FormTitleComponent} from "../common/form-title/form-title.component";

@Component({
  selector: 'app-select-credential',
  standalone: true,
    imports: [
        MatSelectionList,
        MatListOption,
        MatButton,
        FormsModule,
        MatIcon,
        FormTitleComponent
    ],
  templateUrl: './select-credential.component.html',
  styleUrl: './select-credential.component.scss'
})
export class SelectCredentialComponent {

    @ViewChild('selectionList') private readonly _selectionList!: MatSelectionList;
    protected readonly credentialDisplayName = credentialDisplayName;

    constructor(private _authenticationService: AuthenticationService) {
    }

    protected onSubmit()
    {
        const value: MatListOption = this.selectionList.selectedOptions.selected[0];
        if (!value)
        {
            return;
        }
        this._authenticationService.selectCredential(value.value).subscribe();
    }

    private get selectionList(): MatSelectionList {
        return this._selectionList;
    }

    protected get loginData(): LoginData | undefined {
        return this._authenticationService.loginData;
    }

    protected onCancel()
    {
        this._authenticationService.reset();
    }
}
