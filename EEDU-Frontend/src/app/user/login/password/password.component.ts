import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-password', templateUrl: './password.component.html', styleUrls: ['./password.component.scss']
})
export class PasswordComponent
{

    @Output() submit = new EventEmitter<string>();
    @Input() loginName?: string;
    password?: string;
    error?: string;

    showPassword: boolean = false;

    onShowPassword(event: MouseEvent)
    {
        this.showPassword = !this.showPassword;
        event.stopPropagation()
    }

    onSubmit()
    {
        if (!this.password)
        {
            return;
        }
        this.submit.emit(this.password)
    }
}
