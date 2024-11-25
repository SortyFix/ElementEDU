export class ScheduledAppointmentModel {

    public constructor(
        public readonly id: bigint,
        public readonly start: bigint,
        public readonly duration: bigint,
        public readonly period: bigint
    ) {}

    public static fromObject(object: any): ScheduledAppointmentModel {
        return new ScheduledAppointmentModel(
            BigInt(object.id),
            BigInt(object.start),
            BigInt(object.duration),
            BigInt(object.period)
        );
    }
}
