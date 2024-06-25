import { Component } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})

export class SidebarComponent {
    sidebar_buttons = [
        {title:'Georg-August-Zinn-Schule', icon_name: 'school'},
        {title:'Dashboard', icon_name: 'dashboard'},
        {title:'Courses', icon_name:'book_5'},
        {title:'Timetable', icon_name:'calendar_view_week'},
        {title:'Calendar', icon_name: 'calendar_month'},
        {title:'News', icon_name: 'newspaper'},
        {title:'Chat', icon_name: 'forum'},
        {title:'Settings', icon_name: 'settings'}
    ]
}
