import { Component } from '@angular/core';
import {ListItemContent} from "../../../../common/abstract-list/list-item-content";
import {CourseModel} from "../../course-model";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";

@Component({
    imports: [
        MatIcon,
        MatButton,
    ],
  templateUrl: './course-list-item.component.html',
  styleUrl: './course-list-item.component.scss'
})
export class CourseListItemComponent extends ListItemContent<CourseModel>{}
