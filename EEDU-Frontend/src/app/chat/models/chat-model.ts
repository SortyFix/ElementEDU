export class ChatModel {
    constructor(
        public readonly id: bigint,
        public readonly chatTitle: string,
        public readonly timeOfCreation: bigint,
        public readonly users: bigint[],
        public readonly chatHistory: bigint
    ) { }
}
