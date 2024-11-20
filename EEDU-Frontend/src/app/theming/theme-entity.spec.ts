import { ThemeEntity } from './theme-entity';

describe('ThemeEntity', () => {
  it('should create an instance', () => {
    expect(new ThemeEntity(0n, "", 0, 0, 0)).toBeTruthy();
  });
});
