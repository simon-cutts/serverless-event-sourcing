package com.sighware.customer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Person Customer entity used in persistence with the Customers table
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamoDBTable(tableName = "Customer")
public class PersonCustomer extends Customer {

    public PersonCustomer() {
        super();
    }

    public PersonCustomer(String customerId,
                          CustomerName customerName,
                          CustomerAddress address) {
        super(customerId, customerName, address);
    }

    public static PersonCustomer convert(Customer c) {
        PersonCustomer pc = new PersonCustomer(c.getCustomerId(), c.getCustomerName(), c.getCustomerAddress());
        pc.setVersion(c.getVersion());
        return pc;
    }
}
