import {AfterViewInit, ChangeDetectorRef, Component, EventEmitter, input, InputSignal, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {AbstractList, ListItemInfo, SelectionType} from "../../common/abstract-list/abstract-list.component";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatButton, MatIconButton} from "@angular/material/button";
import {EntityService} from "../entity-service";
import {MatDialog} from "@angular/material/dialog";
import {ComponentType} from "@angular/cdk/overlay";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatCheckbox} from "@angular/material/checkbox";
import {AccessibilityService} from "../../accessibility.service";
import {UserService} from "../../user/user.service";
import {GeneralErrorBoxComponent} from "../../common/general-error-box/general-error-box.component";

@Component({
    selector: 'app-entity-list',
    imports: [MatIcon, AbstractList, NgIf, MatProgressBar, MatIconButton, MatButton, MatPaginator, MatCheckbox, GeneralErrorBoxComponent],
    templateUrl: './entity-list.component.html',
    styleUrl: './entity-list.component.scss'
})
export class EntityListComponent<T extends { id: any }> implements AfterViewInit {

    @Output() public readonly deletePressed: EventEmitter<T[]> = new EventEmitter<T[]>;
    public readonly service: InputSignal<EntityService<any, T, any, any> | null> = input<EntityService<any, T, any, any> | null>(null)
    public readonly itemInfo: InputSignal<ListItemInfo<T> | null> = input<ListItemInfo<T> | null>(null);

    protected readonly SelectionType: typeof SelectionType = SelectionType;

    private _paginatorEnabled: boolean = true;
    private _values: readonly T[] = [];
    private _pageIndex: number = 0;
    private _pageSize: number = 10;

    private _pagedValues: readonly T[] = [];

    public constructor(
        private readonly _matDialog: MatDialog,
        private readonly _cdr: ChangeDetectorRef,
        private readonly _accessibilityService: AccessibilityService,
        private readonly _userService: UserService
    ) {}

    protected hasPrivilege(privilege: string | null): boolean
    {
        if(privilege === null)
        {
            return true;
        }
        return this._userService.getUserData.hasPrivilege(privilege);
    }

    protected get paginatorEnabled(): boolean {
        return this._paginatorEnabled;
    }
    protected get paginatorRequired(): boolean
    {
        return this._values.length > 10 && !this._accessibilityService.mobile;
    }

    protected togglePaginator(): void {
        this._paginatorEnabled = !this._paginatorEnabled;
        this.updatePagedValues();
    }

    protected get values(): readonly T[] {
        return this._values;
    }

    protected get pagedValues(): readonly T[] {
        return this._pagedValues;
    }

    protected get pageIndex(): number {
        return this._pageIndex;
    }

    protected set pageIndex(value: number) {
        this._pageIndex = value;
    }

    protected get pageSize(): number {
        return this._pageSize;
    }

    protected set pageSize(value: number) {
        this._pageSize = value;
    }

    protected get loaded(): boolean {
        return this.service()?.fetched || false;
    }

    public ngAfterViewInit(): void {
        if (!this.service()) {
            return;
        }
        this.service()?.value$.subscribe((value: T[]): void => {
            this._values = value;
            this.updatePagedValues();
            this._cdr.detectChanges();
        })
    }

    public openCreateDialog(): void {
        this._matDialog.open(this.service()?.createDialogType as ComponentType<any>, {width: '600px'});
    }

    protected onPageChange(event: PageEvent) {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.updatePagedValues();
    }

    private updatePagedValues(): void {
        if(!this.paginatorEnabled || !this.paginatorRequired)
        {
            this._pagedValues = this._values;
            return;
        }

        const startIndex: number = this.pageIndex * this.pageSize;
        const endIndex: number = startIndex + this.pageSize;
        this._pagedValues = this._values.slice(startIndex, endIndex);
    }
}
