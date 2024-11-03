import {PrivilegeModel} from "./PrivilegeModel";

export class GroupModel
{
    constructor(public readonly id: bigint,
                public readonly name: string,
                public readonly privileges: PrivilegeModel[]) { }
}
