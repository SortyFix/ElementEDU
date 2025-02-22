import {IllnessNotificationStatus} from "../illness-notification-status";
import {FileModel} from "../../file/file-model";
import {ReducedUserModel} from "../../user/reduced-user-model";

export interface GenericIllnessNotificationModel {
    readonly id: bigint;
    readonly user: ReducedUserModel;
    readonly status: IllnessNotificationStatus;
    readonly reason: string;
    readonly timestamp: bigint;
    readonly expirationTime: bigint;
    readonly fileModel: FileModel;
}

export class IllnessNotificationModel {
    constructor(
        readonly id: bigint,
        readonly user: ReducedUserModel,
        readonly status: IllnessNotificationStatus,
        readonly reason: string,
        readonly timestamp: bigint,
        readonly expirationTime: bigint,
        readonly fileModel: FileModel)
    {}

    public static fromObject(obj: GenericIllnessNotificationModel): IllnessNotificationModel
    {
        let fileModel: FileModel = FileModel.fromObject(obj.fileModel);
        let reducedUserModel: ReducedUserModel = ReducedUserModel.fromObject(obj.user);
        return new IllnessNotificationModel(
            obj.id,
            reducedUserModel,
            IllnessNotificationStatus[obj.status as keyof typeof IllnessNotificationStatus],
            obj.reason,
            obj.timestamp,
            obj.expirationTime,
            fileModel
        )
    }

    public get timestampString(): string {
        const date = new Date(Number(this.timestamp) * 1000);
        return date.toLocaleDateString("en-GB", { year: 'numeric', month: 'long', day: 'numeric' });
    }

    public get expirationTimeString(): string {
        const date = new Date(Number(this.expirationTime) * 1000);
        return date.toLocaleDateString("en-GB", { year: 'numeric', month: 'long', day: 'numeric' });
    }
}
