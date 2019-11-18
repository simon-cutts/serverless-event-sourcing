package com.sighware.customer.event;


import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sighware.customer.command.CustomerCreateCommand;
import com.sighware.customer.command.CustomerUpdateCommand;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.error.CustomerUpdateException;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.util.DynamoDBAdapter;
import org.junit.Assert;
import org.junit.Test;

public class PersonCustomerUpdatedEventTest {

    private static final DynamoDBAdapter adapter = DynamoDBAdapter.getInstance();

    @Test
    public void testPersonAmendEvent() throws Exception {

        // create the customer object for post
        PersonCustomer customer = CustomerBuilder.buildPerson();

        CustomerEvent event = new PersonCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        PersonCustomer updateCust = (PersonCustomer) command.persist();

        // now update the customer
        updateCust.getCustomerName().setForeNames("Bill");
        event = new PersonCustomerUpdatedEvent(updateCust);
        CustomerUpdateCommand update = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        update.persist();

        // now retrieve the updated item
        PersonCustomer cust = adapter.getDynamoDBMapper().load(PersonCustomer.class, updateCust.getCustomerId());
        String result = new ObjectMapper().writeValueAsString(cust);

        // Confirm version is now 2
        Assert.assertTrue(result.startsWith("{\"customerId\":\"" + cust.getCustomerId()
                + "\",\"customerName\":{\"title\":\"Mr\",\"foreNames\":\"Bill\",\"surname\":\"Glyn Dŵr\"}"));
        Assert.assertTrue(result.endsWith("\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\"" +
                ",\"postalCode\":\"SA14FR\"}},\"version\":2}"));

        // now decrement version number to force a fail
        cust.setVersion(cust.getVersion() - 1);
        event = new PersonCustomerUpdatedEvent(cust);
        update = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());

        try {
            update.persist();
            Assert.fail("Transaction should have been cancelled");
        } catch (CustomerUpdateException e) {
        }
    }

    @Test
    public void testFailAmendEvent() {

        // create the customer object for post
        PersonCustomer customer = CustomerBuilder.buildPerson();

        CustomerEvent event = new PersonCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        PersonCustomer updateCust = (PersonCustomer) command.persist();

        // now give the wrong customerId to the customer
        updateCust.setCustomerId("does not exist");
        event = new PersonCustomerUpdatedEvent(updateCust);
        CustomerUpdateCommand update = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        try {
            update.persist();
            Assert.fail("Should not have updated the customer");
        } catch (CustomerUpdateException e) {
        }

    }
}
