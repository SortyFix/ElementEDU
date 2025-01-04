import { Component } from '@angular/core';
import {GeneralCreateComponent} from "../general-create-component/general-create.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
  selector: 'app-create-room',
  standalone: true,
    imports: [
        GeneralCreateComponent,
        MatCardContent,
        MatCardActions,
        MatButton,
        MatDialogClose
    ],
  templateUrl: './create-room.component.html',
  styleUrl: './create-room.component.scss'
})
export class CreateRoomComponent {

}
