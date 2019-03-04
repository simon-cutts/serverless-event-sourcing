package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.model.Customer;
import com.sighware.customer.query.CustomerQuery;
import com.sighware.customer.query.EventQuery;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Customer delete command. Deletes the customer and the events for the customer, but persists this delete event
 * to record the activity
 *
 * @author Simon Cutts
 */
public class CustomerDeleteCommand {

    private DynamoDBMapper mapper;

    private CustomerEvent customerEvent;

    public CustomerDeleteCommand(CustomerEvent customerEvent, DynamoDBMapper mapper) {
        this.customerEvent = customerEvent;
        this.mapper = mapper;
    }

    /**
     * Persist the event and customer data. As soon as AWS release Transaction support for DynamoDBMapper, both
     * tables will be saved as one transaction
     */
    public Customer persist() throws CustomerNotFoundException {

        // Confirm the customer exists
        CustomerQuery cc = new CustomerQuery(customerEvent.getCustomerId(), mapper);
        Customer customer = cc.get();

        // TODO: Add transaction support when DynamoDBMapper supports the new transaction API

        // Delete the customer
        mapper.delete(customer);

        // Now get the events to delete
        EventQuery query = new EventQuery(mapper, ZonedDateTime.now(ZoneOffset.UTC), customerEvent.getCustomerId());
        List<CustomerEvent> events = query.get();
        for (CustomerEvent event : events) {
            mapper.delete(event);
        }

        // Save the delete event
        mapper.save(customerEvent);

        return null;
    }
}
