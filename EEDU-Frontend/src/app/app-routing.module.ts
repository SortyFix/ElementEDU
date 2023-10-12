import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginPageComponent} from './login-page/login-page.component';
import {HomePageComponent} from "./home-page/home-page.component";
import {CommonModule} from "@angular/common";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    {path: '', redirectTo: '/home', pathMatch: "full"},
    {path: 'user-login', component: LoginPageComponent},
    {path: 'home', component: HomePageComponent}
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
