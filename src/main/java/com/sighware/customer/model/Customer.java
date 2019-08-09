package com.sighware.customer.model;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Parent Customer entity, needed so that the Customer can be persisted as part of a CustomerEvent
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamoDBDocument
public class Customer {

    private String customerId;
    private String organisationName;
    private CustomerName customerName;
    private CustomerAddress customerAddress;
    private Long version;

    public Customer() {
    }

    /**
     * Create an instance of Customer for a person
     *
     * @param customerId
     * @param customerName
     * @param address
     */
    public Customer(String customerId,
                    CustomerName customerName,
                    CustomerAddress address) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = address;
    }

    /**
     * Create an instance of Customer for an organisation
     *
     * @param customerId
     * @param organisationName
     * @param address
     */
    public Customer(String customerId,
                    String organisationName,
                    CustomerAddress address) {
        this.customerId = customerId;
        this.organisationName = organisationName;
        this.customerAddress = address;
    }

    /**
     * Create a snapshot of the Customer from the sum of its versions
     *
     * @param c
     * @param customerVersions
     */
    public static void snapshot(Customer c, List<Customer> customerVersions) {

        for (Customer cust : customerVersions) {

            c.setCustomerId(cust.getCustomerId());
            c.setVersion(cust.getVersion());
            c.setCustomerName(cust.getCustomerName());
            c.setOrganisationName(cust.getOrganisationName());
            c.setCustomerAddress(cust.getCustomerAddress());
        }
    }

    @DynamoDBHashKey(attributeName = "customerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public CustomerName getCustomerName() {
        return customerName;
    }

    public void setCustomerName(CustomerName customerName) {
        this.customerName = customerName;
    }

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

//    @DynamoDBVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}