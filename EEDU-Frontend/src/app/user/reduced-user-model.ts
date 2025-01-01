import {GroupModel} from "./group/group-model";

export class ReducedUserModel {
    constructor(
        public readonly id: bigint,
        public readonly firstName: string,
        public readonly lastName: string) {
    }
}
