package com.sighware.customer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Organisation Customer entity used in persistence with the Customers table
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamoDBTable(tableName = "Customer")
public class OrganisationCustomer extends Customer {

    public OrganisationCustomer() {
        super();
    }

    public OrganisationCustomer(String customerId,
                                String customerName,
                                CustomerAddress address) {
        super(customerId, customerName, address);
    }

    public static OrganisationCustomer convert(Customer c) {
        OrganisationCustomer pc = new OrganisationCustomer(c.getCustomerId(), c.getOrganisationName(), c.getCustomerAddress());
        pc.setVersion(c.getVersion());
        return pc;
    }
}
