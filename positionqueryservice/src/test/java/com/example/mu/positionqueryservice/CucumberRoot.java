package com.example.mu.positionqueryservice;

import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;




@RunWith(SpringRunner.class)
@SpringBootTest(classes = PositionQueryService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberRoot {

    @Autowired
    protected TestRestTemplate template;

    TestingServer zkServer;

    @Before
    public void before() throws Exception {

        zkServer = new TestingServer(2181, true);

        // demo to show how to add custom header Globally for the http request in spring test template , like IV user header
        template.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders()
                    .add("iv-user", "user");
            return execution.execute(request, body);
        }));


    }

    @After
    public void tearDown() throws Exception
    {
        zkServer.stop();
    }

}
