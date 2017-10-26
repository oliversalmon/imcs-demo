import { Component } from '@angular/core';
import { PriceGetServiceService } from './price-get-service.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

title:string='MU Report UI';
 constructor(private dataService:PriceGetServiceService){

 }

 ngOnInit(){
 }

}
