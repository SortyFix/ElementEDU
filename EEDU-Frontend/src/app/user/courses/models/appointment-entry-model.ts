export class AppointmentEntryModel {
    constructor(public readonly id: bigint, public readonly timeStamp: bigint, public readonly description: string, public readonly homework: string) {}

    public static fromObject(object: any): AppointmentEntryModel {
        const id: bigint = BigInt(object.id);
        return new AppointmentEntryModel(id, BigInt(object.timeStamp), object.description, object.homework);
    }
}
