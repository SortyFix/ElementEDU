import { Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {CoursesComponent} from "./courses/courses.component";
import {TimetableComponent} from "./timetable/timetable.component";
import {NewsComponent} from "./news/news.component";
import {ChatComponent} from "./chat/chat.component";
import {SettingsComponent} from "./settings/settings.component";
import {NewsPageComponent} from "./news/news-page/news-page.component";
import {IllnessNotificationComponent} from "./illness-notification/illness-notification.component";

<<<<<<<< HEAD:EEDU-Frontend/src/app/app.routes.ts
export const routes: Routes = [
========
const routes: Routes = [
    {path: '', redirectTo: '/dashboard', pathMatch: 'full'},
>>>>>>>> implement-userlist:EEDU-Frontend/src/app/app-routing.module.ts
    {path: 'dashboard', component: DashboardComponent},
    {path: 'courses', component: CoursesComponent},
    {path: 'timetable', component: TimetableComponent},
    {path: 'news', component: NewsComponent},
    {path: 'news/:id', component: NewsPageComponent},
    {path: 'news/page/:pageIndex', component: NewsComponent},
    {path: 'chat', component: ChatComponent},
    {path: 'settings', component: SettingsComponent},
    {path: 'illness-notification', component: IllnessNotificationComponent}
];
