package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
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

    private final DynamoDBMapper mapper;

    private final CustomerEvent customerEvent;

    public CustomerDeleteCommand(CustomerEvent customerEvent, DynamoDBMapper mapper) {
        this.customerEvent = customerEvent;
        this.mapper = mapper;
    }

    /**
     * Persist the event and customer data. Uses Transaction support for DynamoDBMapper, so both
     * tables will be modified as one transaction
     */
    public void persist() throws CustomerNotFoundException {

        // Confirm the customer exists
        CustomerQuery cc = new CustomerQuery(customerEvent.getCustomerId(), mapper);
        Customer customer = cc.get();

        TransactionWriteRequest writeRequest = new TransactionWriteRequest();
        writeRequest.addDelete(customer);

        // Now get the events to delete
        EventQuery query = new EventQuery(mapper, ZonedDateTime.now(ZoneOffset.UTC), customerEvent.getCustomerId());
        List<CustomerEvent> events = query.get();
        for (CustomerEvent event : events) {
            writeRequest.addDelete(event);
        }

        // Save the delete event
        writeRequest.addPut(customerEvent);

        mapper.transactionWrite(writeRequest);
    }
}
