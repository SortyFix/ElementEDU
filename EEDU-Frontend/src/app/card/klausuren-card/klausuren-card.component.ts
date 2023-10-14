import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-klausuren-card',
  templateUrl: './klausuren-card.component.html',
  styleUrls: ['./klausuren-card.component.css']
})
export class KlausurenCardComponent {
    @Input() title: string = "Title";
}
