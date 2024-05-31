import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {RequestLoginComponent} from "./user/login/authentication/auth-modal/request/request-login.component";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    //{path: '', pathMatch: "full", component: AppComponent},
    {path: 'request', component: RequestLoginComponent},
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
