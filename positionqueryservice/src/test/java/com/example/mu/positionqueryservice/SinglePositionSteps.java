package com.example.mu.positionqueryservice;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utilities.SingletonTestManager;

import java.util.Iterator;
import java.util.List;

import static com.example.mu.positionqueryservice.PositionQueryService.POSITION_ACCOUNT_MAP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SinglePositionSteps {

    private List<PositionAccount> listOfPositions;
    private ResponseEntity<String> response;
    HttpHeaders headers = new HttpHeaders();

    TestRestTemplate template;
    static HazelcastInstance hazelcastInstanceMember;
    static CuratorFramework cli;
    static TestingServer server;
    String url;

    @Before
    public void before() {





    }

    @Given("^the list of Position Accounts to access a single account$")
    public void the_list_of_Position_Accounts(List<PositionAccount> listOfPositions) {
        this.listOfPositions = listOfPositions;

        Iterator<PositionAccount> iter = listOfPositions.iterator();
        while (iter.hasNext()) {
            PositionAccount acc = iter.next();
            IMap<String, PositionAccount> mapAccount = SingletonTestManager.getHz().getMap(POSITION_ACCOUNT_MAP);
            mapAccount.put(acc.getAccountId() + acc.getInstrumentid(), acc);
        }


    }

    @When("^the client calls /getPositionAccount/\"([^\"]*)\"$")
    public void the_client_calls_getAllPositionAccounts_witAccountId(String positionAccountId) {
        response = SingletonTestManager.getRestTemplate().getForEntity(SingletonTestManager.getURL() + "/getPositionAccount/"+positionAccountId, String.class);

    }

    @Then("^the client receives response status code of for one position (\\d+)$")
    public void the_client_receives_response_code_for_one_position(int reponseCode){
        HttpStatus currentStatusCode = response.getStatusCode();
        assertThat("status codes is incorrect : " + response.getBody(), currentStatusCode.value(), is(reponseCode));
    }


    @And("^response contains only the following position account$")
    public void response_contains_only_the_following_position_accounts(DataTable table) {




    }

    @After
    public void close() {

    }
}
