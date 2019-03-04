package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.model.Customer;
import com.sighware.customer.query.CustomerQuery;
import org.apache.log4j.Logger;

/**
 * Customer update command to handle the persistence of both the Event and its payload.
 *
 * @author Simon Cutts
 */
public class CustomerUpdateCommand {

    private static final Logger LOG = Logger.getLogger(CustomerUpdateCommand.class);

    private DynamoDBMapper mapper;

    private CustomerEvent customerEvent;

    public CustomerUpdateCommand(CustomerEvent customerEvent, DynamoDBMapper mapper) {
        this.customerEvent = customerEvent;
        this.mapper = mapper;
    }

    /**
     * Persist the event and customer data. As soon as AWS release Transaction support for DynamoDBMapper, both
     * tables will be saved as one transaction
     */
    public Customer persist() throws CustomerNotFoundException {

        String customerId = customerEvent.getData().getCustomerId();

        // Confirm customer exists
        CustomerQuery cc = new CustomerQuery(customerId, mapper);
        cc.get();

        // TODO: Add transaction support when DynamoDBMapper supports the new transaction API

        // save the entity first, this is important because its version number then gets populated
        // for the event
        mapper.save(customerEvent.getData());

        // Save the event, with a version number of the customer that corresponds to customer entity
        mapper.save(customerEvent);

        return customerEvent.getData();
    }
}
