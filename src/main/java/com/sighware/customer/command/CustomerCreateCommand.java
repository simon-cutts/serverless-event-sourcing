package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.model.Customer;

/**
 * Customer create command to handle the persistence of both the Event and its payload.
 *
 * @author Simon Cutts
 */
public class CustomerCreateCommand {

    private final DynamoDBMapper mapper;

    private final CustomerEvent customerEvent;

    public CustomerCreateCommand(CustomerEvent customerEvent, DynamoDBMapper mapper) {
        this.customerEvent = customerEvent;
        this.mapper = mapper;
    }

    /**
     * Persist the event and customer data. Uses AWS  Transaction support for DynamoDBMapper, so both
     * tables are saved as one transaction
     */
    public Customer persist() {
        customerEvent.getData().setVersion(new Long(1));

        TransactionWriteRequest writeRequest = new TransactionWriteRequest();
        writeRequest.addPut(customerEvent.getData());
        writeRequest.addPut(customerEvent);
        mapper.transactionWrite(writeRequest);

        return customerEvent.getData();
    }
}
