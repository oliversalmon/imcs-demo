import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { MarketDataAppComponent } from './market-data-app/market-data-app.component';
import { PriceGetServiceService } from './price-get-service.service';
import { MarketDataTableComponent } from './market-data-table/market-data-table.component';
import { PositionTableComponent } from './position-table/position-table.component';
import { TradeCounterComponent } from './trade-counter/trade-counter.component';

@NgModule({
  declarations: [
    AppComponent,
    MarketDataAppComponent,
    MarketDataTableComponent,
    PositionTableComponent,
    TradeCounterComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpModule
  ],
  providers: [PriceGetServiceService],
  bootstrap: [AppComponent]
})
export class AppModule {


}
