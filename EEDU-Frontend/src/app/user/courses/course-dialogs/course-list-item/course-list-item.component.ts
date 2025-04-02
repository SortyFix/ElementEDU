import {Component} from '@angular/core';
import {ListItemContent} from "../../../../common/abstract-list/list-item-content";
import {CourseModel} from "../../course-model";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {MatChip, MatChipAvatar, MatChipSet} from "@angular/material/chips";
import {icons} from "../../../../../environment/styles";
import {NgIf} from "@angular/common";

@Component({
    imports: [
        MatIcon,
        MatButton,
        MatChipSet,
        MatChip,
        MatChipAvatar,
        NgIf,
    ],
    templateUrl: './course-list-item.component.html',
    styleUrl: './course-list-item.component.scss'
})
export class CourseListItemComponent extends ListItemContent<CourseModel> {protected readonly icons = icons;}
