import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TradeCounterComponent } from './trade-counter.component';

describe('TradeCounterComponent', () => {
  let component: TradeCounterComponent;
  let fixture: ComponentFixture<TradeCounterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TradeCounterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TradeCounterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
