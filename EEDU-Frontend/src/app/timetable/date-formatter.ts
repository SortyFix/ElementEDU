import { CalendarDateFormatter, DateFormatterParams } from 'angular-calendar';
import { formatDate } from '@angular/common';
import { Injectable } from '@angular/core';

@Injectable()
export class DateFormatter extends CalendarDateFormatter {

    public override dayViewHour({ date, locale }: DateFormatterParams): string {

        if(!locale)
        {
            throw new Error('locale is required');
        }

        return formatDate(date, 'HH:mm', locale);
    }

    public override weekViewHour({ date, locale }: DateFormatterParams): string {
        return this.dayViewHour({ date, locale });
    }
}
