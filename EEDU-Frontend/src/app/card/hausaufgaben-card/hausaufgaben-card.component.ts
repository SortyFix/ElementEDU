import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-hausaufgaben-card',
  templateUrl: './hausaufgaben-card.component.html',
  styleUrls: ['./hausaufgaben-card.component.css']
})
export class HausaufgabenCardComponent {
    @Input() title: string = "Title";
}
