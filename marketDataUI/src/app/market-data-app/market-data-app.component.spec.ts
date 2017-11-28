import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MarketDataAppComponent } from './market-data-app.component';

describe('MarketDataAppComponent', () => {
  let component: MarketDataAppComponent;
  let fixture: ComponentFixture<MarketDataAppComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MarketDataAppComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketDataAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
