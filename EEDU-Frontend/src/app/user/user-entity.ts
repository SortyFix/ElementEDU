export class UserEntity {

    id: bigint;
    firstName: string;
    lastName: string;

    constructor(id: bigint, firstName: string, lastName: string) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
