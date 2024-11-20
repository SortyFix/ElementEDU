import { ThemeModel } from './theme-model';

describe('ThemeEntity', () => {
  it('should create an instance', () => {
    expect(new ThemeModel(0n, "", 0, 0, 0)).toBeTruthy();
  });
});
