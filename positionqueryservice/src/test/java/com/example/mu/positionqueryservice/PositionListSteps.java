package com.example.mu.positionqueryservice;

import com.example.mu.domain.PositionAccount;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PositionListSteps extends CucumberRoot {

    private List<PositionAccount> listOfPositions;
    private ResponseEntity    response;


    @Given("^the list of Position Accounts$")
    public void the_list_of_Position_Accounts(List<PositionAccount> listOfPositions){
        this.listOfPositions = listOfPositions;
    }

    @When("^the client calls /getAllPositionAccounts$")
    public void the_client_calls_getAllPositionAccounts(){
        response = template.getForEntity("/getAllPositionAccounts", List.class);
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
