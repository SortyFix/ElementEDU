import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    {path: '', redirectTo: '/home', pathMatch: "full"}
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
