package com.sighware.customer.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.event.CustomerEvents;
import com.sighware.customer.model.Customer;
import com.sighware.customer.query.CustomerQuery;
import com.sighware.customer.query.EventQuery;
import com.sighware.customer.util.Alive;
import com.sighware.customer.util.DynamoDBAdapter;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;


/**
 * Query controller api endpoints
 */
@RestController
@EnableWebMvc
@RequestMapping("/customer/v1/query")
public class QueryController {

    private static final Logger LOG = Logger.getLogger(QueryController.class);

    private final DynamoDBMapper mapper = DynamoDBAdapter.getInstance().getDynamoDBMapper();

    /**
     * Get a customer
     *
     * @param customerId CustomerId to retrieve
     * @return a Customer
     */
    @RequestMapping(path = "/get/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getCustomer(@PathVariable("customerId") String customerId) {

        try {
            CustomerQuery cc = new CustomerQuery(customerId, mapper);
            Customer customer = cc.get();
            return ResponseEntity.ok(customer);
        } catch (CustomerNotFoundException e) {
            LOG.info("Unable to find Customer Id " + customerId);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get the CustomerEvents
     *
     * @param customerId CustomerId to retrieve
     * @return a Customer
     */
    @RequestMapping(path = "/event/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<CustomerEvents> getEvents(@PathVariable("customerId") String customerId) {

        try {
            CustomerQuery cc = new CustomerQuery(customerId, mapper);
            cc.get();

            EventQuery query = new EventQuery(mapper, customerId);
            List<CustomerEvent> events = query.get();

            return ResponseEntity.ok(new CustomerEvents((events)));
        } catch (CustomerNotFoundException e) {
            LOG.info("Unable to find Customer Id " + customerId);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Test service is alive
     *
     * @return Alive
     */
    @RequestMapping(path = "/alive", method = RequestMethod.GET)
    public Alive alive() {
        return new Alive(true);
    }
}

