export class MessageModel {
    constructor(
        public readonly id: bigint,
        public readonly authorId: bigint,
        public readonly body: string,
        public readonly timeStamp: bigint
    ) {}
}
