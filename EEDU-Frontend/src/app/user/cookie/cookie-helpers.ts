import {CookieOptions} from "./cookie-options";

export class CookieHelpers {
    /**
     * Create a cookie with the name, value and options.
     *
     * Maps the given data including CookieOptions data into a proper cookie string.
     *
     * @param name - The name of the cookie to create
     * @param value - The value to set for the cookie
     * @param options - An instance of the CookieOptions class containing correct option data (for HttpOnly or Secure for example).
     */
    public createCookie(name: string, value: string, options: CookieOptions): string
    {
        const cookie: string  = `${name}=${value}; ${options.toInjectableString()}`
        document.cookie = cookie;
        return cookie;
    }

    /**
     * Get a cookie value by extracting it from the cookie pool string.
     *
     * This function iterates through all cookies by mapping them onto a struct. If the given name matches a cookie's
     * name, it's value will be decoded and output.
     *
     * @param name - The name of the cookie to get its value from.
     */
    public getCookieValue(name: string): string | null
    {
        const cookies: string[] = document.cookie.split('; ');

        for(const cookie of cookies) {
            const [cookieName, cookieValue] = cookie.split('=');
            if (cookieName === name) {
                return decodeURIComponent(cookieValue);
            }
        }

        return null;
    }

    /**
     * Remove a cookie by setting its expiry date in the past.
     *
     * @param name - The cookie to remove
     */
    public removeCookie(name: string)
    {
        document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;`;
    }
}
