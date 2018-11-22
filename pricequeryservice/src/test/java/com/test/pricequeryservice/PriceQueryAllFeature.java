package com.test.pricequeryservice;

import com.example.mu.domain.Price;
import com.example.mu.pricequeryservice.controllers.StartUp;
import com.example.mu.pricequeryservice.repository.PriceRepository;
import com.hazelcast.core.IMap;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utilities.SingletonTestManager;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PriceQueryAllFeature {


    private List<Price> listOfPrices;
    private ResponseEntity<String> response;

    @Before
    public void before() throws Exception {
       SingletonTestManager.startUpServices(StartUp.class);
    }

    @Given("^the list of Prices$")
    public void the_list_of_Prices(List<Price> listOfPrices) {
        this.listOfPrices = listOfPrices;

        Iterator<Price> iter = listOfPrices.iterator();
        while (iter.hasNext()) {
            Price px = iter.next();
            IMap<String, Price> priceMap = SingletonTestManager.getHz().getMap(PriceRepository.PRICE_MAP);
            priceMap.put(px.getPriceId() + px.getInstrumentId(), px);
        }
    }

    @When("^the client says /getAllPrices$")
    public void the_client_calls_getAllPositionAccounts() {
        response = SingletonTestManager.getRestTemplate().getForEntity(SingletonTestManager.getURL() + "/getAllPrices", String.class);

    }

    @Then("^the client receives response status code of (\\d+)")
    public void the_client_receives_reponse_status_code(int statusCde){

        HttpStatus currentStatusCode = response.getStatusCode();
        assertThat("status codes is incorrect : " + response.getBody(), currentStatusCode.value(), is(statusCde));

    }

    @And("^response contains the above list of Prices$")
    public void the_response_contains_the_above_list_of_Prices(){

        String body = response.getBody();

        Iterator<Price> iter = listOfPrices.iterator();
        while (iter.hasNext()) {
            Price px = iter.next();
            assertThat("price id matches", body.contains(px.getPriceId()));
            assertThat("instrument id matches", body.contains(px.getInstrumentId()));
            assertThat("px matches", body.contains(new Double(px.getPrice()).toString()));
        }

    }
}
