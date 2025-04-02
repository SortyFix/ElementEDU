import {Component, input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {EntityListComponent} from "../../entity/entity-list/entity-list.component";
import {ComponentType} from "@angular/cdk/overlay";
import {EntityService} from "../../entity/entity-service";
import {ListItemInfo} from "../../common/abstract-list/abstract-list.component";

export interface LazyLoadedAccordionTab {
    label: string;
    icon: string;
    service: EntityService<any, any, any, any>;
    itemInfo: ListItemInfo<any>;
    deleteDialog: ComponentType<any>,
}

@Component({
    selector: 'app-lazy-loaded-accordion',
    imports: [MatIcon, MatTabLabel, MatTabContent, EntityListComponent, MatTab, MatTabGroup],
    templateUrl: './lazy-loaded-accordion.component.html',
    styleUrl: './lazy-loaded-accordion.component.scss'
})
export class LazyLoadedAccordionComponent {

    public readonly tabs: InputSignal<readonly LazyLoadedAccordionTab[]> = input<readonly LazyLoadedAccordionTab[]>([]);
}
