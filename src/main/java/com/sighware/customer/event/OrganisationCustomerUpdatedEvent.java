package com.sighware.customer.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.OrganisationCustomer;

public class OrganisationCustomerUpdatedEvent extends CustomerEvent {

    private OrganisationCustomer organisationCustomer;

    public OrganisationCustomerUpdatedEvent(OrganisationCustomer organisationCustomer) {
        super("OrganisationCustomerUpdatedEvent", organisationCustomer.getCustomerId());
        this.organisationCustomer = organisationCustomer;
    }

    @DynamoDBHashKey(attributeName = "customerId")
    public String getCustomerId() {
        return organisationCustomer.getCustomerId();
    }

    public Customer getData() {
        return organisationCustomer;
    }

}