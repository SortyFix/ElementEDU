import {Component} from '@angular/core';
import {ListItemContent} from "../../../../common/abstract-list/list-item-content";
import {CourseModel} from "../../course-model";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {MatChip, MatChipSet} from "@angular/material/chips";

@Component({
    imports: [
        MatIcon,
        MatButton,
        MatChipSet,
        MatChip,
    ],
    templateUrl: './course-list-item.component.html',
    styleUrl: './course-list-item.component.scss'
})
export class CourseListItemComponent extends ListItemContent<CourseModel> {}
