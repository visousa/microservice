package com.example.microservice;

import com.example.microservice.controller.PurchaseControllerTest;
import com.example.microservice.service.PurchaseServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by bruno on 05-11-2016.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    PurchaseServiceTest.class,
    PurchaseControllerTest.class,
})
public class MicroserviceTestSuite {
}
