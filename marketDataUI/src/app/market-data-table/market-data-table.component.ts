import { Component, OnInit, Input } from '@angular/core';
//import { Price } from '../price';
import { PriceGetServiceService } from '../price-get-service.service';
import { Observable } from 'rxjs/Observable';
import {
  trigger,
  state,
  style,
  animate,
  transition,
  group
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
     
      state('left',   
      style({ scrollLeft: '+=300' })),
      state('right',   
      style({ scrollRight: '-=300' })),
      transition('left <=> right', animate('600ms ease-out'))
    ]),
  
    trigger('dummyAnimation',[
      state('small', style({
        transform: 'scale(1)',
      })),
      state('large', style({
        transform: 'scale(1.5)',
      })),
      transition('small<=>large', animate('300ms ease-in'))
    ]),
      
  ]
})



export class MarketDataTableComponent implements OnInit {

  prices: Observable<Price>;
  priceMove: Observable<Number>;
  state: string = 'active';
  

  
  prevPrice:Observable<Price>;
  private visible:string="visible";
  constructor(private priceService:PriceGetServiceService) { }

  @Input() visibility:string = this.visible; //this will trigger the component to animate from void => visible

  ngOnInit() {

    //this.prices = this.priceService.getPrices();
    //console.log('Prices are '+this.prices[0]);
    //this.priceService.requestPrices();
    this.prices = this.priceService.getPrice();
    this.priceMove = this.priceService.getPriceMove();
    this.prevPrice = this.priceService.getPrevPrices();
    this.visibility = this.visible; 

  }

 changeOfState(){
    this.visible = (this.visible==='visible' ? 'left' : 'right');
    
  }

  doAnimate(){
    this.state = (this.state === 'active' ? 'inactive': 'activate');
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
