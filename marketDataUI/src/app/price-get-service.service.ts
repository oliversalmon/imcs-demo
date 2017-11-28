import { Injectable, NgZone, OnInit } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subscription } from 'rxjs/Subscription';
import 'rxjs/Rx';
import 'rxjs/add/operator/map';
import * as EventSource from 'eventsource';

import 'rxjs/add/operator/toPromise';



class Price {

  priceId: string;
  instrumentId: string;
  price: number;
  timeStamp: number;


  constructor(jsonData) {
    Object.assign(this, jsonData);
  }

};

@Injectable()
export class PriceGetServiceService {


  private priceURL = '/pricequeryservice/pricestream';
  private delayThrottle = 0;
  price: Observable<Price>;
  priceMove: Observable<Number>;
  prevPricesAsObservable: Observable<Price>;
  private prevPrices: Price[];
  private _priceMove: BehaviorSubject<Number> = new BehaviorSubject<Number>(null);
  private _priceSource: EventSource;
  private _prices: BehaviorSubject<Price> = new BehaviorSubject<Price>(null);
  private _prevPrice: BehaviorSubject<Price> = new BehaviorSubject<Price>(null);


  constructor(private http: Http, private _zone: NgZone) {

    this._priceSource = this.createPriceSource();
    this.price = this.createPriceObservable();
    this.priceMove = this.createPriceMoveObservable();
    this.prevPricesAsObservable = this.createPrevPriceObservable();
    this.prevPrices = new Array();

  }

  getPriceMove(): Observable<Number> {
    return this.priceMove;
  }

  getPrice(): Observable<Price> {
    return this.price;
  }

  getPrevPrices():Observable<Price>{
    return this.prevPricesAsObservable;
  }

  private createPriceMoveObservable(): Observable<Number>{
    return this._priceMove.asObservable().throttleTime(this.delayThrottle);
  }

  private createPriceObservable(): Observable<Price> {
    return this._prices.asObservable().throttleTime(this.delayThrottle);
  }

  private createPrevPriceObservable(): Observable<Price>{
    return this._prevPrice.asObservable().throttleTime(this.delayThrottle);
  }

  private createPriceSource(): EventSource {
    const priceSource = new EventSource(this.priceURL);
    
    priceSource.onmessage = sse => {
      const price: Price = new Price(JSON.parse(sse.data));
      this._zone.run(() => {
        
        this._prices.next(price);
        this._priceMove.next(this.calculatePriceMove(price)); 
        //console.log(this.priceMove);
        //console.log(this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()].instrumentId);
        //console.log(this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()].price);
        
        this.updateAndStorePrevPrice(price);

        //mover the previous price along
        
        //console.log(this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()].instrumentId);
        //console.log(this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()].price);
        this._prevPrice.next(this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()]);
      })
    };
    priceSource.onerror = err => this._prices.error(err);
    priceSource.oncomplete = comp => console.log('completed...');

    return priceSource;



  }

  private calculatePriceMove(price: Price): Number {

    if (this.findPrevPrice(price.instrumentId).valueOf() != -1){

      //console.log('Prev price is '+this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()].price    );
      //console.log('Current price is '+price.price);
      return this.prevPrices[this.findPrevPrice(price.instrumentId).valueOf()].price - price.price;


    }
      
      return 0;
   }

  private findPrevPrice(symbol: String): Number {

    if(this.prevPrices.length < 0) return -1;
    
    return this.prevPrices.findIndex(prevPrice => {
      return prevPrice.instrumentId == symbol

    })



  }

  private updateAndStorePrevPrice(currentPrice: Price) {

    var prevPriceIndex = this.findPrevPrice(currentPrice.instrumentId);

    if (prevPriceIndex != -1) {

      //found it update
      this.prevPrices[prevPriceIndex.valueOf()].price = currentPrice.price;
      //console.log(this.prevPrices[prevPriceIndex.valueOf()].price);
    } else {

      //else add
      this.prevPrices.push(currentPrice);
    }

    //console.log(this.prevPrices.length);

  }


}
