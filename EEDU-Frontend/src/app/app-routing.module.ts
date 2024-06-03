import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {TestComponent} from "./user/login/test/test.component";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    //{path: '', pathMatch: "full", component: AppComponent},
    {path: 'test', component: TestComponent},
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
