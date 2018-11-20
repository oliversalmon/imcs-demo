package com.example.mu.positionqueryservice;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.core.IMap;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Iterator;
import java.util.List;

import static com.example.mu.positionqueryservice.PositionQueryService.POSITION_ACCOUNT_MAP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PositionListSteps extends CucumberRoot {

    private List<PositionAccount> listOfPositions;
    private ResponseEntity<String>    response;
    HttpHeaders headers = new HttpHeaders();

    @Given("^the list of Position Accounts$")
    public void the_list_of_Position_Accounts(List<PositionAccount> listOfPositions){
        this.listOfPositions = listOfPositions;

        Iterator<PositionAccount> iter = listOfPositions.iterator();
        while(iter.hasNext()){
            PositionAccount acc = iter.next();
            IMap<String, PositionAccount> mapAccount = hazelcastInstanceMember.getMap(POSITION_ACCOUNT_MAP);
            mapAccount.put(acc.getAccountId()+acc.getInstrumentid(), acc);
        }
    }

    @When("^the client calls /getAllPositionAccounts$")
    public void the_client_calls_getAllPositionAccounts(){
          response = template.getForEntity("/getAllPositionAccounts", String.class);

    }

    @Then("the client receives response status code of (\\d+)$")
    public void the_client_receives_response_status_code_of(int statusCode) {
        HttpStatus currentStatusCode = response.getStatusCode();
        assertThat("status codes is incorrect : "+ response.getBody(), currentStatusCode.value(), is(statusCode));
    }

    @And("^receives the following list of Position Accounts$")
    public void receives_the_following_list_of_Position_Accounts(DataTable lists) {


    }

}
