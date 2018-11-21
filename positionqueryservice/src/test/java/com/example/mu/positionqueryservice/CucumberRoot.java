package com.example.mu.positionqueryservice;

import cucumber.api.java.After;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@Suite.SuiteClasses({ PositionListSteps.class })
//@SpringBootTest(classes = StartUp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"requireHz=true"})
public class CucumberRoot {






    @After
    public void tearDown() {

    }

}
