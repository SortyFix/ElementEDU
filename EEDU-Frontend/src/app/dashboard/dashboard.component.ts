import {Component, inject} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {map} from 'rxjs/operators';

@Component({
    selector: 'app-dashboard', templateUrl: './dashboard.component.html', styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    private breakpointObserver = inject(BreakpointObserver);

    /** Based on the screen size, switch from standard to one column per row */
    cardLayout = this.breakpointObserver.observe(Breakpoints.Handset).pipe(map(({matches}) => {
        // If device is in portrait mode
        if (matches) {
            return {
                marginTop: "margintop-mobile",
                columns: 2,
                rowHeight: "300px",
                stundenplan: { cols: 2, rows: 1 },
                klausuren: { cols: 1, rows: 1 },
                hausaufgaben: { cols: 1, rows: 1 },
                chat: { cols: 0, rows: 0 }
            };
        }
        // If device is in landscape mode
        return{
            marginTop: "margintop-desktop",
            columns: 2,
            rowHeight: "350px",
            klausuren: { cols: 1, rows: 1 },
            stundenplan: { cols: 1, rows: 2 },
            hausaufgaben: { cols: 1, rows: 1 },
            chat: {cols: 1, rows: 2 }
        }
    }));
}
