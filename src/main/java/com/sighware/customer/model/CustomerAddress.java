package com.sighware.customer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamoDBDocument
public class CustomerAddress {

    private String addressType;
    private Address address;

    public CustomerAddress() {
    }

    public CustomerAddress(String addressType, Address address, String postalCode) {
        this.addressType = addressType;
        this.address = address;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
