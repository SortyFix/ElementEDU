import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {PasswordComponent} from "./user/login/password/password.component";
import {RequestLoginComponent} from "./user/login/request/request-login.component";
import {AppComponent} from "./app.component";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    //{path: '', pathMatch: "full", component: AppComponent},
    { path: 'login', component: RequestLoginComponent },
    { path: 'password', component: PasswordComponent }
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
