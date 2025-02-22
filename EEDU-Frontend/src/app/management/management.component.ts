import {Component, OnInit, Type} from '@angular/core';
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";

export interface CourseTab
{
    label: string,
    icon: string,
    component: Type<any>
}

@Component({
    selector: 'app-management',
    imports: [
        UserListComponent,
        MatAccordion,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle,
        MatExpansionPanelDescription,
        NgForOf,
        MatButton,
        NgIf,
        MatTab,
        MatTabGroup,
        MatTabContent,
        MatTabLabel,
        MatIcon,
        NgForOf,
        NgComponentOutlet,
    ],
    templateUrl: './management.component.html',
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    private readonly _courseComponentsTabs: CourseTab[] = [
        { label: 'Courses', icon: icons.course, component: CourseListComponent },
        { label: 'Class Rooms', icon: icons.classroom, component: ClassRoomListComponent },
        { label: 'Rooms', icon: icons.room, component: RoomListComponent },
        { label: 'Subjects', icon: icons.subject, component: SubjectListComponent }
    ];

    userList: UserModel[] = [];

    illnessNotifications: IllnessNotificationModel[] = []

    PREFIX: string = "http://localhost:8080/api/v1";

    public constructor(protected userService: UserService, , protected fileService: FileService, private http: HttpClient) {}

    public ngOnInit(): void {
        this.getPendingNotifications();
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }

    protected get courseComponentTabs(): CourseTab[] {
        return this._courseComponentsTabs;
    }

    private getPendingNotifications(): void
    {
        this.http.get<GenericIllnessNotificationModel[]>(`${this.PREFIX}/illness/management/get-pending`, {
            withCredentials: true
        }).subscribe((list: GenericIllnessNotificationModel[]): void => {
            list.forEach((obj: GenericIllnessNotificationModel): void => {
                let model: IllnessNotificationModel = IllnessNotificationModel.fromObject(obj);
                this.illnessNotifications.push(model);
            })
            console.log(this.illnessNotifications);
        })
    }

    public downloadFile(id: bigint)
    {
        this.fileService.downloadFile(id);
    }
}

