package com.sighware.customer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.CustomerBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QueryControllerTest {

    private static Customer customer;
    private static Customer orgCustomer;
    QueryController controller = new QueryController();

    @BeforeClass
    public static void setup() {

        // Create some customers
        CommandController controller = new CommandController();
//        System.out.println(controller.addPersonCustomer(CustomerBuilder.buildPerson()).getBody());

        customer = controller.addPersonCustomer(CustomerBuilder.buildPerson()).getBody();
        orgCustomer = controller.addOrganisationCustomer(CustomerBuilder.buildOrganistion()).getBody();
    }

    @Test
    public void testQueryPersonCustomer() throws JsonProcessingException {

        ResponseEntity<Customer> r = controller.getCustomer(customer.getCustomerId());
        Customer cust = r.getBody();

        // Check customer is persisted and has a version of 1
        assertNotNull(cust);

//        String result = new ObjectMapper().writeValueAsString(cust);
//        System.out.println(result);

        assertEquals(customer.getCustomerId(), cust.getCustomerId());
    }

    @Test
    public void testQueryOrgCustomer() throws JsonProcessingException {

        ResponseEntity<Customer> r = controller.getCustomer(orgCustomer.getCustomerId());
        Customer cust = r.getBody();

        // Check customer is persisted and has a version of 1
        assertNotNull(cust);

//        String result = new ObjectMapper().writeValueAsString(cust);
//        System.out.println(result);

        assertEquals(orgCustomer.getCustomerId(), cust.getCustomerId());
    }

    @Test
    public void testNotFound() {

        String id = UUID.randomUUID().toString();
        ResponseEntity<Customer> response = controller.getCustomer(id);
        assertEquals(ResponseEntity.badRequest().build(), response);

    }
}
