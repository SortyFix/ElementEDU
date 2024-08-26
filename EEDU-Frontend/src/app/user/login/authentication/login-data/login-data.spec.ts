import { LoginData } from './login-data';

describe('LoginData', () => {
  it('should create an instance', () => {
    expect(new LoginData("test", [])).toBeTruthy();
  });
});
