package com.example.mu.positionqueryservice;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SocketUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.example.mu.positionqueryservice.PositionQueryService.POSITION_ACCOUNT_MAP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PositionListSteps  {

    private List<PositionAccount> listOfPositions;
    private ResponseEntity<String>    response;
    HttpHeaders headers = new HttpHeaders();

    TestRestTemplate template;
    HazelcastInstance hazelcastInstanceMember;
    CuratorFramework cli;
    TestingServer server;
    String url;

    @Before
    public void before() throws  Exception{


        int zkPort = SocketUtils.findAvailableTcpPort();
        server = new TestingServer(2181);
        cli = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryOneTime(2000));
        cli.start();
        hazelcastInstanceMember = Hazelcast.newHazelcastInstance();

        int port = SocketUtils.findAvailableTcpPort(zkPort+1);

        ConfigurableApplicationContext context = new SpringApplicationBuilder(StartUp.class).run(
                "--server.port="+port,
                "--management.endpoints.web.expose=*",
                "--requireHz=true",
                "--spring.cloud.zookeeper.connect-string=localhost:" + 2181);

        url= "http://localhost:"+port;
        template = new TestRestTemplate();
        template.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders()
                    .add("iv-user", "user");
            return execution.execute(request, body);
        }));





    }

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
          response = template.getForEntity(url+"/getAllPositionAccounts", String.class);

    }

    @Then("the client receives response status code of (\\d+)$")
    public void the_client_receives_response_status_code_of(int statusCode) {
        HttpStatus currentStatusCode = response.getStatusCode();
        assertThat("status codes is incorrect : "+ response.getBody(), currentStatusCode.value(), is(statusCode));
    }

    @And("^response contains the above list of Position Accounts$")
    public void response_contains_the_above_list_of_Position_Accounts() {

        String body = response.getBody();

        Iterator<PositionAccount> iter = listOfPositions.iterator();
        while(iter.hasNext()){
            PositionAccount acc = iter.next();
            assertThat("account id matches",body.contains(acc.getAccountId()));
            assertThat("instrument id matches",body.contains(acc.getInstrumentid()));
            assertThat("pnl matches",body.contains(new Double(acc.getPnl()).toString()));
        }




    }

    @After
    public void close() {
        //cli.close();
        //server.close();
    }

}
