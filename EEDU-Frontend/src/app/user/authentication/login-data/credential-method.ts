export enum CredentialMethod {
    PASSWORD = "PASSWORD",
    EMAIL = "EMAIL",
    SMS = "SMS",
    TOTP = "TOTP"
}

export const CredentialMethodDisplayNames = {
    [CredentialMethod.PASSWORD]: {icon: 'lock', displayName: 'Password'},
    [CredentialMethod.EMAIL]: {icon: 'email', displayName: 'Email'},
    [CredentialMethod.SMS]: {icon: 'message', displayName: 'SMS'},
    [CredentialMethod.TOTP]: {icon: 'qr_code_2', displayName: 'TOTP (Authenticator App)'}
};

export function credentialDisplayName(method: CredentialMethod): { icon: string, displayName: string } {
    return CredentialMethodDisplayNames[method as CredentialMethod] || 'Unknown';
}
