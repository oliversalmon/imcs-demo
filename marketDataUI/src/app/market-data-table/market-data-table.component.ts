import { Component, OnInit } from '@angular/core';
//import { Price } from '../price';
import { PriceGetServiceService } from '../price-get-service.service';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-market-data-table',
  templateUrl: './market-data-table.component.html',
  styleUrls: ['./market-data-table.component.css']
})



export class MarketDataTableComponent implements OnInit {

  prices: Observable<Price>;
  constructor(private priceService:PriceGetServiceService) { }

  ngOnInit() {

    //this.prices = this.priceService.getPrices();
    //console.log('Prices are '+this.prices[0]);
    //this.priceService.requestPrices();
    this.prices = this.priceService.getPrice();

  }

}
class Price {

 //priceId: string;
 //instrumentId: string;
 //price: number;
 //timeStamp:  number;

 constructor(priceId: string,
  instrumentId: string,
  price: number,
  timeStamp:  number) {

   }
}
