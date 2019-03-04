package com.sighware.customer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Person Customer entity for read model
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamoDBDocument
public class CustomerName {

    // Class specific variables
    private String title;
    private String foreNames;
    private String surname;

    public CustomerName() {
    }

    public CustomerName(String title,
                        String foreNames,
                        String surname) {
        this.title = title;
        this.foreNames = foreNames;
        this.surname = surname;
    }

    public String getForeNames() {
        return foreNames;
    }

    public void setForeNames(String foreNames) {
        this.foreNames = foreNames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return String.format("Customer [title=%s, foreNames=%s, surname=%s]",
                title, foreNames, surname);
    }
}
