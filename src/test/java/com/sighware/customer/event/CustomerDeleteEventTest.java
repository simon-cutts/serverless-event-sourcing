package com.sighware.customer.event;


import com.sighware.customer.command.CustomerCreateCommand;
import com.sighware.customer.command.CustomerDeleteCommand;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.query.CustomerQuery;
import com.sighware.customer.util.DynamoDBAdapter;
import org.junit.Assert;
import org.junit.Test;

public class CustomerDeleteEventTest {

    private static DynamoDBAdapter adapter = DynamoDBAdapter.getInstance();

    @Test
    public void testDeleteEvent() {

        // create the customer object for post
        PersonCustomer customer = CustomerBuilder.buildPerson();

        CustomerEvent event = new PersonCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        PersonCustomer updateCust = (PersonCustomer) command.persist();

        // now update the customer

        try {
            CustomerDeleteEvent deleteEvent = new CustomerDeleteEvent(updateCust.getCustomerId());
            CustomerDeleteCommand deleteCommand = new CustomerDeleteCommand(deleteEvent, adapter.getDynamoDBMapper());
            deleteCommand.persist();
        } catch (Exception e) {
            Assert.fail("Customer should have been deleted");
        }

        try {
            CustomerQuery cc = new CustomerQuery(updateCust.getCustomerId(), adapter.getDynamoDBMapper());
            cc.get();
            Assert.fail("Customer should not exist anymore");
        } catch (CustomerNotFoundException e) {
            Assert.assertTrue(true);
        }
    }
}
