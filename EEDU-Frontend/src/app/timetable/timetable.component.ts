import {Component, OnInit} from '@angular/core';
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
    MatTable
} from "@angular/material/table";
import {CourseService} from "../user/courses/course.service";

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        MatTable,
        MatColumnDef,
        MatHeaderCell,
        MatCell,
        MatCellDef,
        MatHeaderCellDef,
        MatHeaderRow,
        MatRow,
        MatRowDef,
        MatHeaderRowDef
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{
    displayedColumns: string[] = ['time', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'];
    timeSlots = ['08:00 AM', '09:00 AM', '10:00 AM', '11:00 AM', '12:00 PM', '01:00 PM', '02:00 PM', '03:00 PM', '04:00 PM'];

    constructor(private courseService: CourseService) {}

    ngOnInit(): void {
        this.courseService.fetchCourses().subscribe((value) => {
            console.log(value);
        })
    }
}
