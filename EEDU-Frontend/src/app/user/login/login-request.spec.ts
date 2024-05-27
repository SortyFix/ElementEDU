import { LoginRequest } from './login-request';

describe('LoginRequest', () => {
  it('should create an instance', () => {
    expect(new LoginRequest("", "", false)).toBeTruthy();
  });
});
