import { Component } from '@angular/core';
import {GeneralCreateComponent} from "../general-create-component/general-create.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
  selector: 'app-create-subject',
  standalone: true,
    imports: [
        GeneralCreateComponent,
        MatCardContent,
        MatCardActions,
        MatButton,
        MatDialogClose
    ],
  templateUrl: './create-subject.component.html',
  styleUrl: './create-subject.component.scss'
})
export class CreateSubjectComponent {

}
