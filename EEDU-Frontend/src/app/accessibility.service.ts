import {HostListener, Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AccessibilityService {

    private _mobile: boolean = false;


    constructor() {
        this.checkScreenSize();
        window.addEventListener('resize', (): void => this.checkScreenSize())
    }

    /**
     * Returns whether this is currently in mobile mode or not
     * @protected
     */
    public get mobile(): boolean {
        return this._mobile;
    }

    /**
     * Checks the screen size and sets the mobile property based on the width.
     */
    private checkScreenSize(): void {
        this._mobile = window.innerWidth <= 600 || window.innerHeight <= 600;
    }
}
