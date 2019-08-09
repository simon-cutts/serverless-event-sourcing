package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTransactionWriteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.model.Customer;
import com.sighware.customer.query.CustomerQuery;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Customer update command to handle the persistence of both the Event and its payload.
 *
 * @author Simon Cutts
 */
public class CustomerUpdateCommand {

    private static final Logger LOG = Logger.getLogger(CustomerUpdateCommand.class);

    private final DynamoDBMapper mapper;

    private final CustomerEvent customerEvent;

    public CustomerUpdateCommand(CustomerEvent customerEvent, DynamoDBMapper mapper) {
        this.customerEvent = customerEvent;
        this.mapper = mapper;
    }

    /**
     * Persist the event and customer data. Uses AWS Transaction support for DynamoDBMapper, so both
     * tables are saved as one transaction
     */
    public Customer persist() throws CustomerNotFoundException {

        String customerId = customerEvent.getData().getCustomerId();

        // Confirm customer exists
        CustomerQuery cc = new CustomerQuery(customerId, mapper);
        Customer c = cc.get();

        // increment version
        customerEvent.getData().setVersion(c.getVersion() + 1);


        DynamoDBTransactionWriteExpression exp = new DynamoDBTransactionWriteExpression()
                .withConditionExpression("attribute_exists(version)");

        TransactionWriteRequest writeRequest = new TransactionWriteRequest();
        writeRequest.addUpdate(customerEvent.getData(),exp);
        writeRequest.addPut(customerEvent);
        mapper.transactionWrite(writeRequest);

        return customerEvent.getData();
    }
}
