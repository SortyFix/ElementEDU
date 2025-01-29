import { TestBed } from '@angular/core/testing';

import { ArticleCreationService } from './article-creation.service';

describe('ArticleCreationService', () => {
  let service: ArticleCreationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ArticleCreationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
