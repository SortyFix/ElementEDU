export class TotpData {
    constructor(readonly loginName: string, readonly secret: string, readonly algorithm: string,
                readonly digits: number, readonly period: number)
    {}

    public get toUrl(): string
    {
        return `otpauth://totp/${this.loginName}?secret=${this.secret}&issuer=ElementEDU&algorithm=${this.algorithm}&digits=${this.digits}&period=${this.period}`;
    }
}
