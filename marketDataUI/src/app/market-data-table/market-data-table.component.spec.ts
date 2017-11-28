import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MarketDataTableComponent } from './market-data-table.component';

describe('MarketDataTableComponent', () => {
  let component: MarketDataTableComponent;
  let fixture: ComponentFixture<MarketDataTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MarketDataTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarketDataTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
