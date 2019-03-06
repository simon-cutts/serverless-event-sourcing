package com.sighware.customer.controller;

import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.event.CustomerEvents;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.PersonCustomer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QueryControllerTest {

    private static Customer customer;
    private static Customer orgCustomer;
    private final QueryController controller = new QueryController();

    @BeforeClass
    public static void setup() {
        // Create some customers
        CommandController controller = new CommandController();

        customer = controller.addPersonCustomer(CustomerBuilder.buildPerson()).getBody();
        orgCustomer = controller.addOrganisationCustomer(CustomerBuilder.buildOrganistion()).getBody();
    }

    @Test
    public void testQueryPersonCustomer() {
        ResponseEntity<Customer> r = controller.getCustomer(customer.getCustomerId());
        Customer cust = r.getBody();

        // Check customer is persisted and has a version of 1
        assertNotNull(cust);
        assertEquals(customer.getCustomerId(), cust.getCustomerId());
    }

    @Test
    public void testQueryOrgCustomer() {
        ResponseEntity<Customer> r = controller.getCustomer(orgCustomer.getCustomerId());
        Customer cust = r.getBody();

        // Check customer is persisted and has a version of 1
        assertNotNull(cust);
        assertEquals(orgCustomer.getCustomerId(), cust.getCustomerId());
    }

    @Test
    public void testNotFound() {
        String id = UUID.randomUUID().toString();
        ResponseEntity<Customer> response = controller.getCustomer(id);
        assertEquals(ResponseEntity.badRequest().build(), response);
    }

    @Test
    public void testGetEvents() {

        // Add an update events to the original create event
        customer.getCustomerName().setForeNames("Bill");
        new CommandController().updatePersonCustomer(PersonCustomer.convert(customer));

        ResponseEntity<CustomerEvents> r = controller.getEvents(customer.getCustomerId());
        CustomerEvent event = Objects.requireNonNull(r.getBody()).getEvents().get(0);
        assertEquals("PersonCustomerCreatedEvent", event.getEventName());
        event = r.getBody().getEvents().get(1);
        assertEquals("PersonCustomerUpdatedEvent", event.getEventName());
    }
}
