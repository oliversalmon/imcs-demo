import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { MarketDataAppComponent } from './market-data-app/market-data-app.component';
import { PriceGetServiceService } from './price-get-service.service';
import { MarketDataTableComponent } from './market-data-table/market-data-table.component';

@NgModule({
  declarations: [
    AppComponent,
    MarketDataAppComponent,
    MarketDataTableComponent
  ],
  imports: [
    BrowserModule,
    HttpModule
  ],
  providers: [PriceGetServiceService],
  bootstrap: [AppComponent]
})
export class AppModule {


}
