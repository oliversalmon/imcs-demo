import { Component, OnInit, Input } from '@angular/core';
//import { Price } from '../price';
import { PriceGetServiceService } from '../price-get-service.service';
import { Observable } from 'rxjs/Observable';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';


@Component({
  selector: 'app-market-data-table',
  templateUrl: './market-data-table.component.html',
  styleUrls: ['./market-data-table.component.css'],
  host: {
    '[@visibilityChanged]': 'visibility'
  },
  animations: [
    trigger('visibilityChanged', [
     
      state('visible',   
      style({ scrollLeft: '0' })),
      transition('visible => visible', animate('600ms ease-out')),
      transition('void => visible', animate(300))
    ])
  ]
})



export class MarketDataTableComponent implements OnInit {

  prices: Observable<Price>;
  private visible:string="visible";
  constructor(private priceService:PriceGetServiceService) { }

  @Input() visibility:string = this.visible; //this will trigger the component to animate from void => visible

  ngOnInit() {

    //this.prices = this.priceService.getPrices();
    //console.log('Prices are '+this.prices[0]);
    //this.priceService.requestPrices();
    this.prices = this.priceService.getPrice();
    this.visibility = this.visible; 

  }

  get changeOfState(){
    return this.visible? 'visible':'';
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
