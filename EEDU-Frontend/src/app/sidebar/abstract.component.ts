import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-sidebar',
  templateUrl: './abstract.component.html',
  styleUrls: ['./abstract.component.scss']
})

export class AbstractComponent {
    constructor(public router: Router) { }

    sidebar_buttons = [
        {title:'Georg-August-Zinn-Schule', icon_name: 'school', route:'school'},
        {title:'Dashboard', icon_name: 'dashboard', route:'dashboard'},
        {title:'Courses', icon_name:'book_5', route: 'courses'},
        {title:'Timetable', icon_name:'calendar_view_week', route: 'timetable'},
        {title:'Calendar', icon_name: 'calendar_month', route: 'calendar'},
        {title:'News', icon_name: 'newspaper', route: 'news'},
        {title:'Chat', icon_name: 'forum', route: 'chat'},
        {title:'Settings', icon_name: 'settings', route: 'settings'}
    ]
}
