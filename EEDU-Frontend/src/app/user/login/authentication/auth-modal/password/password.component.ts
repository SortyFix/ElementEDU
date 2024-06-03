import {
    AfterViewInit,
    Component,
    ElementRef,
    EventEmitter,
    Input, NgZone,
    Output,
    ViewChild
} from '@angular/core';

@Component({
    selector: 'app-password', templateUrl: './password.component.html', styleUrls: ['./password.component.scss']
})
export class PasswordComponent implements AfterViewInit
{
    @ViewChild("passwordInput") passwordField?: ElementRef;
    @Output() submit = new EventEmitter<string>();
    @Input() loginName?: string;
    @Input() error?: string;
    password?: string;

    showPassword: boolean = false;
    canSubmit: boolean = true;

    constructor(private ngZone: NgZone) {}

    ngAfterViewInit() {
        this.ngZone.runOutsideAngular(() => {
            setTimeout(() => {
                this.passwordField?.nativeElement.select();
                this.passwordField?.nativeElement.focus();
            });
        });
    }

    allowSubmit()
    {
        this.canSubmit = true;
    }

    onShowPassword(event: MouseEvent)
    {
        this.showPassword = !this.showPassword;
        event.stopPropagation()
    }

    onSubmit()
    {
        if (!this.password || !this.canSubmit)
        {
            return;
        }
        this.submit.emit(this.password)
        this.canSubmit = false;
    }
}
