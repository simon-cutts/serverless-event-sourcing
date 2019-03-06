package com.sighware.customer.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.PersonCustomer;

import java.util.UUID;

public class PersonCustomerCreatedEvent extends CustomerEvent {

    private final PersonCustomer personCustomer;

    public PersonCustomerCreatedEvent(PersonCustomer personCustomer) {
        super("PersonCustomerCreatedEvent", personCustomer.getCustomerId());
        this.personCustomer = personCustomer;

        // Generate the UUID
        personCustomer.setCustomerId(UUID.randomUUID().toString());
    }

    @DynamoDBHashKey(attributeName = "customerId")
    public String getCustomerId() {
        return personCustomer.getCustomerId();
    }

    public Customer getData() {
        return personCustomer;
    }

}