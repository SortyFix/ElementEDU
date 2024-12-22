import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';
import {routes} from "./app.routes";
import {provideHttpClient, withFetch} from "@angular/common/http";
import {provideAnimations} from "@angular/platform-browser/animations";
import {provideDateFnsAdapter} from "@angular/material-date-fns-adapter";
import {CalendarModule, DateAdapter} from "angular-calendar";

import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';

export const appConfig: ApplicationConfig = {
  providers: [
      provideZoneChangeDetection({ eventCoalescing: true }),
      provideRouter(routes),
      provideAnimations(),
      provideDateFnsAdapter(),
      provideHttpClient(withFetch()),
      importProvidersFrom(CalendarModule.forRoot({
          provide: DateAdapter,
          useFactory: adapterFactory,
      }))
  ]
};
