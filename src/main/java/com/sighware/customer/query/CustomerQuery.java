package com.sighware.customer.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.OrganisationCustomer;
import com.sighware.customer.model.PersonCustomer;
import org.apache.log4j.Logger;

/**
 * Customer query to retrieve a customer
 *
 * @author Simon Cutts
 */
public class CustomerQuery {

    private static final Logger LOG = Logger.getLogger(CustomerQuery.class);

    private final DynamoDBMapper mapper;

    private final String customerId;

    public CustomerQuery(String customerId, DynamoDBMapper mapper) {
        this.customerId = customerId;
        this.mapper = mapper;
    }

    /**
     * Get the customer
     */
    public Customer get() throws CustomerNotFoundException {

        Customer customer;
        try {
            customer = mapper.load(PersonCustomer.class, customerId);
        } catch (DynamoDBMappingException e) {
            customer = mapper.load(OrganisationCustomer.class, customerId);
        }

        if (customer == null) {
            throw new CustomerNotFoundException("Unable to find Customer Id " + customerId);
        }

        return customer;
    }
}
