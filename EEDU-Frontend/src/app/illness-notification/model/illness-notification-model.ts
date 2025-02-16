import {IllnessNotificationStatus} from "../illness-notification-status";

export interface IllnessNotificationModel {
    readonly id: bigint;
    readonly userId: bigint;
    readonly status: IllnessNotificationStatus;
    readonly timestamp: bigint;
    readonly expirationTime: bigint;
}
