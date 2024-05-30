import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {LoginComponent} from "./user/login/login.component";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    //{path: '', redirectTo: '/home', pathMatch: "full"},
    { path: 'login', component: LoginComponent }
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
