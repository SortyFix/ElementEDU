import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {CoursesComponent} from "./courses/courses.component";
import {TimetableComponent} from "./timetable/timetable.component";
import {NewsComponent} from "./news/news.component";
import {ChatComponent} from "./chat/chat.component";
import {SettingsComponent} from "./settings/settings.component";
import {NewsPageComponent} from "./news/news-page/news-page.component";

const routes: Routes = [
    {path: '', redirectTo: '/dashboard', pathMatch: 'full'},
    {path: 'dashboard', component: DashboardComponent},
    {path: 'courses', component: CoursesComponent},
    {path: 'timetable', component: TimetableComponent},
    {path: 'news', component: NewsComponent},
    {path: 'news/:id', component: NewsPageComponent},
    {path: 'news/page/:pageIndex', component: NewsComponent},
    {path: 'chat', component: ChatComponent},
    {path: 'settings', component: SettingsComponent}
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
