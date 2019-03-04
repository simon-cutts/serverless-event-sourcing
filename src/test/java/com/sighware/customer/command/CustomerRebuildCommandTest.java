package com.sighware.customer.command;


import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.*;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.OrganisationCustomer;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.util.DynamoDBAdapter;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class CustomerRebuildCommandTest {

    private static DynamoDBAdapter adapter = DynamoDBAdapter.getInstance();

    @Test
    public void testPersonRebuild() throws Exception {

        // create the customer object for post
        PersonCustomer customer = CustomerBuilder.buildPerson();

        CustomerEvent event = new PersonCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        PersonCustomer updateCust = (PersonCustomer) command.persist();

//        System.out.println(JsonConverter.toJson(updateCust));

        // now update the customer with Bill
        updateCust.getCustomerName().setForeNames("Bill");
        event = new PersonCustomerUpdatedEvent(updateCust);
        String timestamp = event.getCreateTime();
        CustomerUpdateCommand updatecommand = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (PersonCustomer) updatecommand.persist();

//        System.out.println(JsonConverter.toJson(updateCust));

        // now update the customer again with Bong
        updateCust.getCustomerName().setSurname("Bong");
        event = new PersonCustomerUpdatedEvent(updateCust);
        updatecommand = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (PersonCustomer) updatecommand.persist();

//        System.out.println(JsonConverter.toJson(updateCust));

        // Test full rebuild for Bill Bong
        CustomerRebuildCommand rebuild = new CustomerRebuildCommand(updateCust.getCustomerId(), adapter.getDynamoDBMapper());
        Customer c = rebuild.rebuild();
        assertEquals("Bill", c.getCustomerName().getForeNames());
        assertEquals("Bong", c.getCustomerName().getSurname());
        assertEquals(new Long("3"), c.getVersion());

        // Now test from a point in time rebuild for Bill Glyn Dŵr
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp);
        rebuild = new CustomerRebuildCommand(adapter.getDynamoDBMapper(), updateCust.getCustomerId(), zonedDateTime);
        c = rebuild.rebuild();
        assertEquals("Bill", c.getCustomerName().getForeNames());
        assertEquals("Glyn Dŵr", c.getCustomerName().getSurname());
        assertEquals(new Long("2"), c.getVersion());
    }

    @Test
    public void testOrgRebuild() throws Exception {

        // create the customer object for post
        OrganisationCustomer customer = CustomerBuilder.buildOrganistion();

        CustomerEvent event = new OrganisationCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        OrganisationCustomer updateCust = (OrganisationCustomer) command.persist();

//        System.out.println(JsonConverter.toJson(updateCust));

        // now update the customer with 2222222222222222
        updateCust.setOrganisationName("2222222222222222");
        event = new OrganisationCustomerUpdatedEvent(updateCust);
        String timestamp = event.getCreateTime();
        CustomerUpdateCommand updatecommand = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (OrganisationCustomer) updatecommand.persist();

//        System.out.println(JsonConverter.toJson(updateCust));

        // now update the customer again with 333333333333333
        updateCust.setOrganisationName("333333333333333");
        event = new OrganisationCustomerUpdatedEvent(updateCust);
        updatecommand = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (OrganisationCustomer) updatecommand.persist();

//        System.out.println(JsonConverter.toJson(updateCust));

        // Test full rebuild for 333333333333333
        CustomerRebuildCommand rebuild = new CustomerRebuildCommand(updateCust.getCustomerId(), adapter.getDynamoDBMapper());
        Customer c = rebuild.rebuild();
        assertEquals("333333333333333", c.getOrganisationName());
        assertEquals(new Long("3"), c.getVersion());

        // Now test from a point in time rebuild for 2222222222222222
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp);
        rebuild = new CustomerRebuildCommand(adapter.getDynamoDBMapper(), updateCust.getCustomerId(), zonedDateTime);
        c = rebuild.rebuild();
        assertEquals("2222222222222222", c.getOrganisationName());
        assertEquals(new Long("2"), c.getVersion());
    }

    @Test
    public void testDateToEarly() {
        OrganisationCustomer customer = CustomerBuilder.buildOrganistion();

        CustomerEvent event = new OrganisationCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        OrganisationCustomer updateCust = (OrganisationCustomer) command.persist();

        ZonedDateTime inThePast = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

        Customer c = null;
        try {
            CustomerRebuildCommand rebuild = new CustomerRebuildCommand(adapter.getDynamoDBMapper(), updateCust.getCustomerId(), inThePast);
            c = rebuild.rebuild();
            fail("Should not have found a customer for that date");
        } catch (CustomerNotFoundException e) {
            assertNull(c);
        }

    }
}
