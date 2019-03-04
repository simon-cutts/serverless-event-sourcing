package com.sighware.customer.command;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.log4j.Logger;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.OrganisationCustomer;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.query.CustomerQuery;
import com.sighware.customer.query.EventQuery;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Rebuilds a Customer by re-playing the event stream from a point in time. The persistence of the Customer
 * in the database remains unaffected. The customer is a snapshot view only from a point in time
 *
 * @author Simon Cutts
 */
public class CustomerRebuildCommand {

    private static final Logger LOG = Logger.getLogger(CustomerRebuildCommand.class);

    private static final String startTime = "1900-01-01T00:00:01.000Z";

    private DynamoDBMapper mapper;
    private String customerId;
    private String endTime;

    /**
     * Rebuild a customer from before and up unto the timestamp
     *
     * @param mapper
     * @param customerId
     * @param timestamp
     */
    public CustomerRebuildCommand(DynamoDBMapper mapper, String customerId, ZonedDateTime timestamp) {
        this.mapper = mapper;
        this.customerId = customerId;
        this.endTime = timestamp.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Rebuild a customer from now
     *
     * @param customerId
     * @param mapper
     */
    public CustomerRebuildCommand(String customerId, DynamoDBMapper mapper) {
        this(mapper, customerId, ZonedDateTime.now(ZoneOffset.UTC));
    }

    /**
     * Builds a view of customer from a point in time
     */
    public Customer rebuild() throws CustomerNotFoundException {

        // Confirm the customer exists
        CustomerQuery cc = new CustomerQuery(customerId, mapper);
        cc.get();

        LOG.info("Query customer events with customerId " + customerId);
        EventQuery query = new EventQuery(mapper, ZonedDateTime.parse(endTime), customerId);
        List<CustomerEvent> events = query.get();

        // Create the target Customer and collect the different versions of the customer from the events
        Customer customer = null;
        List<Customer> customerVersions = new ArrayList<Customer>();
        for (CustomerEvent event : events) {

            if (customer == null) {
                if (event.getData().getCustomerName() != null) {
                    customer = new PersonCustomer();
                } else {
                    customer = new OrganisationCustomer();
                }
            }

            customerVersions.add(event.getData());
        }

        if (customer == null) {
            throw new CustomerNotFoundException("Unable to find Customer Id " + customerId + " from " + endTime);
        }

        Customer.snapshot(customer, customerVersions);
        return customer;
    }
}
