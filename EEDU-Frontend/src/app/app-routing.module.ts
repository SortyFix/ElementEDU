import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {Authentication} from "./user/login/authentication/authentication.component";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    //{path: '', pathMatch: "full", component: AppComponent},
    {path: 'authorize', component: Authentication},
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
