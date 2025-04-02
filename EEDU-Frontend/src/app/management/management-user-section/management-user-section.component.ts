import {Component} from '@angular/core';
import {
    LazyLoadedAccordionComponent, LazyLoadedAccordionTab
} from "../lazy-loaded-accordion/lazy-loaded-accordion.component";
import {UserService} from "../../user/user.service";
import {GroupService} from "../../user/group/group.service";
import {UserModel} from "../../user/user-model";
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle} from "@angular/material/card";
import {PrivilegeService} from "../../user/group/privilege/privilege.service";

@Component({
    selector: 'management-user-section',
    imports: [LazyLoadedAccordionComponent, MatCard, MatCardSubtitle, MatCardTitle, MatCardHeader, MatCardContent],
    templateUrl: './management-user-section.component.html',
    styleUrl: './management-user-section.component.scss'
})
export class ManagementUserSectionComponent {

    private readonly _tabs: LazyLoadedAccordionTab[];

    public constructor(userService: UserService, groupService: GroupService, privilegeService: PrivilegeService) {
        const idTitle: (obj: { id: string }) => string = (obj: { id: string }): string => obj.id

        this._tabs = [{
            label: 'Users',
            service: userService,
            deleteDialog: null as any,
            icon: 'person',
            itemInfo: {
                title: (entry: UserModel): string => entry.loginName,
                chips: (entry: UserModel): string[] => {
                    return [entry.name, entry.accountType];
                }
            }
        }, {
            label: 'Groups',
            service: groupService,
            deleteDialog: null as any,
            icon: 'persons',
            itemInfo: {
                title: idTitle
            }
        }, {
            label: 'Privileges',
            service: privilegeService,
            deleteDialog: null as any,
            icon: 'key',
            itemInfo: {
                title: idTitle
            }
        }];
    }

    public get tabs(): LazyLoadedAccordionTab[] {
        return this._tabs;
    }
}
