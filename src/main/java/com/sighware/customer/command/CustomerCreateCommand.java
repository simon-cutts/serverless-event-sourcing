package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sighware.customer.model.Customer;
import com.sighware.customer.event.CustomerEvent;

/**
 * Customer create command to handle the persistence of both the Event and its payload.
 *
 * @author Simon Cutts
 */
public class CustomerCreateCommand {

    private DynamoDBMapper mapper;

    private CustomerEvent customerEvent;

    public CustomerCreateCommand(CustomerEvent customerEvent, DynamoDBMapper mapper) {
        this.customerEvent = customerEvent;
        this.mapper = mapper;
    }

    /**
     * Persist the event and customer data. As soon as AWS release Transaction support for DynamoDBMapper, both
     * tables will be saved as one transaction
     */
    public Customer persist() {

        // TODO: Add transaction support when DynamoDBMapper supports the new transaction API

        // save the entity first, this is important because then its version number then gets populated
        // for the event
        mapper.save(customerEvent.getData());

        // Save the event, with a version number of the customer that corresponds to customer entity
        mapper.save(customerEvent);

        return customerEvent.getData();
    }
}
