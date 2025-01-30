import {Component, input, InputSignal} from '@angular/core';
import {RoomModel} from "../../../user/courses/room/room-model";

@Component({
  selector: 'app-room-tab',
  standalone: true,
  imports: [],
  templateUrl: './room-tab.component.html',
  styleUrl: './room-tab.component.scss'
})
export class RoomTabComponent {

    public editing: InputSignal<boolean> = input<boolean>(false);
    public readonly room: InputSignal<RoomModel | undefined> = input<RoomModel | undefined>();

}
