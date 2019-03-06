package com.sighware.customer.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.PersonCustomer;

public class PersonCustomerUpdatedEvent extends CustomerEvent {

    private final PersonCustomer personCustomer;

    public PersonCustomerUpdatedEvent(PersonCustomer personCustomer) {
        super("PersonCustomerUpdatedEvent", personCustomer.getCustomerId());
        this.personCustomer = personCustomer;
    }

    @DynamoDBHashKey(attributeName = "customerId")
    public String getCustomerId() {
        return personCustomer.getCustomerId();
    }

    public Customer getData() {
        return personCustomer;
    }

}