import { UserModel } from './user-model';

describe('UserEntity', () => {
  it('should create an instance', () => {
    expect(new UserModel(0n, "", "")).toBeTruthy();
  });
});
