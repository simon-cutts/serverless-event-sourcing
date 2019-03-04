package com.sighware.customer.event;

public class CustomerDeleteEvent extends CustomerEvent {

    public CustomerDeleteEvent(String customerId) {
        super("CustomerDeleteEvent", customerId);
    }
}