package com.example.mu.positionqueryservice;

import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;




@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartUp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"requireHz=true"})
public class CucumberRoot {



    @Autowired
    protected TestRestTemplate template;

    @Autowired
    protected HazelcastInstance hazelcastInstanceMember;


    CucumberRoot(){
        System.out.println("##### Starting up ...");
    }
    @BeforeClass
    public void before() {


        // demo to show how to add custom header Globally for the http request in spring test template , like IV user header
        template.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders()
                    .add("iv-user", "user");
            return execution.execute(request, body);
        }));


    }

    @After
    public void tearDown() {

    }

}
