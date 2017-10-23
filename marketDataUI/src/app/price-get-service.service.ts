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
  price: Observable<Price>;
  private _priceSource: EventSource;
  private _prices: BehaviorSubject<Price> = new BehaviorSubject<Price>(null)


  constructor(private http: Http, private _zone: NgZone) {

    this._priceSource = this.createPriceSource();
    this.price = this.createPriceObservable();

  }










  getPrice(): Observable<Price> {
    return this.price;
  }

  private createPriceObservable(): Observable<Price> {
    return this._prices.asObservable();
  }

  private createPriceSource(): EventSource {
    const priceSource = new EventSource(this.priceURL);
    priceSource.onmessage = sse => {
      const price: Price = new Price(JSON.parse(sse.data));
      this._zone.run(() => this._prices.next(price));
    };
    priceSource.onerror = err => this._prices.error(err);
    priceSource.oncomplete = comp => this.createPriceSource();

    return priceSource;



  }


}
