import {Component, input, InputSignal} from '@angular/core';
import {AssignmentInsightModel} from "../../../../user/courses/appointment/entry/assignment/assignment-insight-model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-insight-list',
    imports: [
        NgIf
    ],
  templateUrl: './insight-list.component.html',
  styleUrl: './insight-list.component.scss'
})
export class InsightListComponent {

    public readonly insight: InputSignal<AssignmentInsightModel | null> = input<AssignmentInsightModel | null>(null);


}
