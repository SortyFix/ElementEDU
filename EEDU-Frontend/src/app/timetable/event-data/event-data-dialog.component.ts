import {Component, Inject} from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";
import {RoomService} from "../../user/courses/room/room.service";
import {RoomModel} from "../../user/courses/room/room-model";
import {NgIf} from "@angular/common";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatHint} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AssignmentTabComponent} from "./assignment-tab/assignment-tab.component";
import {
    DateTimePickerComponent
} from "../../user/courses/appointment/create-appointment/date-time-picker/date-time-picker.component";
import {EventTileContentComponent} from "./event-tile-content/event-tile-content.component";
import {RoomTabComponent} from "./room-tab/room-tab.component";
import {SelectionInput} from "../../common/selection-input/selection-input.component";
import {
    MAT_DIALOG_DATA, MatDialogActions, MatDialogContent,
} from "@angular/material/dialog";
import {CourseService} from "../../user/courses/course.service";
import {CourseModel} from "../../user/courses/course-model";
import {GeneralCardComponent} from "../../common/general-card-component/general-card.component";

@Component({
    standalone: true,
    imports: [ReactiveFormsModule, NgIf, MatHint, MatGridList, MatGridTile, MatFormField, MatInput, MatButton, AssignmentTabComponent, DateTimePickerComponent, EventTileContentComponent, RoomTabComponent, SelectionInput, MatDialogContent, MatDialogActions, GeneralCardComponent],
    templateUrl: './event-data-dialog.component.html',
    styleUrl: './event-data-dialog.component.scss'
})
export class EventDataDialogComponent {

    private readonly _event!: AppointmentEntryModel;
    private readonly _title: string;
    private readonly _rooms: RoomModel[] = [];

    public constructor(roomService: RoomService,
                       @Inject(MAT_DIALOG_DATA) data: { title: string, appointment: AppointmentEntryModel },
                       private readonly _courseService: CourseService
    ) {
        roomService.value$.subscribe((rooms: RoomModel[]): void => {
            this._rooms.length = 0;
            this._rooms.push(...rooms);
        });

        this._title = data.title;
        this._event = data.appointment;
    }

    protected get course(): CourseModel {
        return this._courseService.findCourseLazily(this.event.course) as CourseModel; // expect the course to exist
    }

    protected get title(): string {
        return this._title;
    }

    protected get event(): AppointmentEntryModel {
        return this._event;
    }

    protected get rooms(): RoomModel[] {
        return this._rooms;
    }
}
