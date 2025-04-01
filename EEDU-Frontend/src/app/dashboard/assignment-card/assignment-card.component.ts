import { Component } from '@angular/core';
import {AssignmentModel} from "../../user/courses/appointment/entry/assignment-model";
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {UserService} from "../../user/user.service";
import {UserModel} from "../../user/user-model";

@Component({
  selector: 'app-assignment-card',
  standalone: true,
    imports: [
        MatButton,
        MatIcon
    ],
  templateUrl: './assignment-card.component.html',
  styleUrl: './assignment-card.component.scss'
})
export class AssignmentCardComponent {
    public constructor(public userService: UserService) {}

    public getUserData(): UserModel {
        return this.userService.getUserData;
    }

    public logout(): void {
        this.userService.logout().subscribe(value => console.log(value));
    }
}
