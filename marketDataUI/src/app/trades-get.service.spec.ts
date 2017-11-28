import { TestBed, inject } from '@angular/core/testing';

import { TradesGetService } from './trades-get.service';

describe('TradesGetService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TradesGetService]
    });
  });

  it('should be created', inject([TradesGetService], (service: TradesGetService) => {
    expect(service).toBeTruthy();
  }));
});
