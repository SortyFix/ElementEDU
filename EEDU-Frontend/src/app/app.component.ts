import {Component, ViewEncapsulation} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {AuthModalComponent} from "./user/login/auth-modal/auth-modal.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent {

    constructor(public dialog: MatDialog) {
    }

    openDialog()
    {
        const dialogRef = this.dialog.open(AuthModalComponent, {
            width: '400px',
            backdropClass: 'blur-background'
        })
        dialogRef.afterClosed().subscribe(result =>
        {
            console.log("Closed lol")
        })
    }
}
