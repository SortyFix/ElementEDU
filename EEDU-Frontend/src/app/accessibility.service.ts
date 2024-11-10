import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AccessibilityService {

    private _mobile: boolean = false;
    private _dimensions: {width: number, height: number} = {width: 1000, height: 1000};

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
        this._dimensions = {width: window.innerWidth, height: window.innerHeight};
        this._mobile = this._dimensions.width <= 650 || this._dimensions.height <= 600;
    }

    public get dimensions(): { width: number; height: number } {
        return this._dimensions;
    }
}
