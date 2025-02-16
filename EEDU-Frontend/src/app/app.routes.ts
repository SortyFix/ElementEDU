import { Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {TimetableComponent} from "./timetable/timetable.component";
import {NewsComponent} from "./news/news.component";
import {ChatComponent} from "./chat/chat.component";
import {SettingsComponent} from "./settings/settings.component";
import {NewsPageComponent} from "./news/news-page/news-page.component";
import {IllnessNotificationComponent} from "./illness-notification/illness-notification.component";
import {ManagementComponent} from "./management/management.component";

export const routes: Routes = [
    {path: '', redirectTo: '/dashboard', pathMatch: 'full'},
    {path: 'dashboard', component: DashboardComponent},
    {path: 'timetable', component: TimetableComponent},
    {path: 'news', component: NewsComponent},
    {path: 'news/:id', component: NewsPageComponent},
    {path: 'news/page/:pageIndex', component: NewsComponent},
    {path: 'chat', component: ChatComponent},
    {path: 'settings', component: SettingsComponent},
    {path: 'illness-notification', component: IllnessNotificationComponent},
    {path: 'management', component: ManagementComponent}
];
