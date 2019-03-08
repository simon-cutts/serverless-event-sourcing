package com.sighware.customer.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sighware.customer.model.Customer;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * CustomerEvents are events created for all command activities. Its immutability is not expressed
 * in this class, but the app treats all customer events as immutable.
 */
@DynamoDBTable(tableName = "CustomerEvent")
public class CustomerEvent {

    private String customerId;
    private String createTime;
    private String eventId;
    private String eventName;
    private Customer data;

    public CustomerEvent() {
    }

    public CustomerEvent(String eventName, String customerId) {
        eventId = UUID.randomUUID().toString();
        createTime = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        this.eventName = eventName;
        this.customerId = customerId;
    }

    public CustomerEvent(String eventName, String customerId, Customer data) {
        this(eventName, customerId);
        this.data = data;
    }

    @DynamoDBHashKey(attributeName = "customerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

//    @JsonProperty("timestamp")
    @DynamoDBRangeKey(attributeName = "createTime")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Customer getData() {
        return data;
    }

    public void setData(Customer data) {
        this.data = data;
    }
}
