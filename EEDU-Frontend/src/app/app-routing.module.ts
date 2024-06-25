import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonModule} from "@angular/common";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {CoursesComponent} from "./pages/courses/courses.component";
import {TimetableComponent} from "./pages/timetable/timetable.component";
import {CalendarComponent} from "./pages/calendar/calendar.component";
import {NewsComponent} from "./pages/news/news.component";
import {SettingsComponent} from "./pages/settings/settings.component";
import {ChatComponent} from "./pages/chat/chat.component";
import {SchoolComponent} from "./pages/school/school.component";

const routes: Routes = [
    // set 'home' as main page if URL is /localhost:4200/
    {path: '', redirectTo: '/home', pathMatch: "full"},
    {path: 'school', component: SchoolComponent},
    {path: 'dashboard', component: DashboardComponent},
    {path: 'courses', component: CoursesComponent},
    {path: 'timetable', component: TimetableComponent},
    {path: 'calendar', component: CalendarComponent},
    {path: 'news', component: NewsComponent},
    {path: 'chat', component: ChatComponent},
    {path: 'settings', component: SettingsComponent},
];

@NgModule({
    imports: [CommonModule,
              RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
