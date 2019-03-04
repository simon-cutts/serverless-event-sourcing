package com.sighware.customer.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.OrganisationCustomer;

import java.util.UUID;

public class OrganisationCustomerCreatedEvent extends CustomerEvent {

    private OrganisationCustomer orgCustomer;

    public OrganisationCustomerCreatedEvent(OrganisationCustomer orgCustomer) {
        super("OrganisationCustomerCreatedEvent", orgCustomer.getCustomerId());
        this.orgCustomer = orgCustomer;

        // Generate the UUID
        orgCustomer.setCustomerId(UUID.randomUUID().toString());
    }

    @DynamoDBHashKey(attributeName = "customerId")
    public String getCustomerId() {
        return orgCustomer.getCustomerId();
    }

    public Customer getData() {
        return orgCustomer;
    }

}