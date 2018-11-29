package com.trade.injector;



import com.example.mu.domain.Trade;
import com.trade.injector.controller.TradeInjectorController;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import utilities.SingletonTestManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InjectTradeFeature {

    List<Trade> listOfGivenTrades = new ArrayList<Trade>();

    @Before
    public void before() throws Exception {


        SingletonTestManager.startUpServices(TradeInjectorController.class);


    }

    @Given("the list of trades")
    public void the_list_of_trades(List<Trade> listOfTrades) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
       listOfGivenTrades = listOfTrades;
    }

    @When("the client injects trades")
    public void the_client_injects_trades() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the client receives exact match of success acks")
    public void the_client_receives_exact_match_of_success_acks() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
