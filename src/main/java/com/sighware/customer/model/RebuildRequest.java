package com.sighware.customer.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class RebuildRequest {
    private String customerId;
    private String timestamp;

    public RebuildRequest() {
    }

    public RebuildRequest(String customerId, ZonedDateTime timestamp) {
        this.customerId = customerId;
        this.timestamp = timestamp.format(DateTimeFormatter.ISO_INSTANT);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
