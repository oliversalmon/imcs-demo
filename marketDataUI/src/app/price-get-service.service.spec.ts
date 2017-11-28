import { TestBed, inject } from '@angular/core/testing';

import { PriceGetServiceService } from './price-get-service.service';

describe('PriceGetServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PriceGetServiceService]
    });
  });

  it('should be created', inject([PriceGetServiceService], (service: PriceGetServiceService) => {
    expect(service).toBeTruthy();
  }));
});
