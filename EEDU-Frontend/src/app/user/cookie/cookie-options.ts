import {SameSite} from "./same-site";

export class CookieOptions {
    secure: boolean = false;
    httpOnly: boolean = false;
    sameSite: SameSite = SameSite.LAX;
    partitioned: boolean = false;
    expires: string | null = null;
    maxAge: number | null = null;
    domain: string | null = null;
    path: string | null = null;

    public toInjectableString() {
        return `${this.secure ? "Secure;" : ""}${this.httpOnly ? "HttpOnly;" : ""}SameSite=${this.sameSite};${this.partitioned ? "Partitioned;" : ""}${this.expires ? `Expires=${this.expires};` : ""}${this.maxAge ? `Max-Age=${this.maxAge};` : ""}${this.domain ? `Domain=${this.domain};` : ""}`;
    }
}
