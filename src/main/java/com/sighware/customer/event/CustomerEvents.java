package com.sighware.customer.event;

import java.util.List;

public class CustomerEvents {

    private final List<CustomerEvent> events;

    public CustomerEvents(List<CustomerEvent> events) {
        this.events = events;
    }

    public List<CustomerEvent> getEvents() {
        return events;
    }
}
