export class AppointmentEntryModel {

    constructor(public readonly id: number,
                public readonly timeStamp: number,
                public readonly description: string,
                public readonly homework: string) {}

    public static fromObject(object: any): AppointmentEntryModel {
        return new AppointmentEntryModel(object.id, object.timeStamp, object.description, object.homework);
    }
}
