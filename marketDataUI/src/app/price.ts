export class Price {

  priceId: string;
  instrumentId: string;
  price: number;
  timeStamp:  number;

  constructor(jsonData) {
    Object.assign(this, jsonData);
    }


}
