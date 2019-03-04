package com.sighware.customer.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sighware.customer.command.CustomerCreateCommand;
import com.sighware.customer.command.CustomerDeleteCommand;
import com.sighware.customer.command.CustomerRebuildCommand;
import com.sighware.customer.command.CustomerUpdateCommand;
import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.CustomerDeleteEvent;
import com.sighware.customer.model.Customer;
import com.sighware.customer.util.DynamoDBAdapter;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.sighware.customer.event.OrganisationCustomerCreatedEvent;
import com.sighware.customer.event.PersonCustomerCreatedEvent;
import com.sighware.customer.event.PersonCustomerUpdatedEvent;
import com.sighware.customer.model.OrganisationCustomer;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.model.RebuildRequest;

import java.time.ZonedDateTime;

/**
 * Command controller to handle REST POST requests. Each request is turned into an event. Each event
 * is then processed by a command. Most methods (or REST endpoint) returns a persisted version of the entity that has been
 * created or amended, because that entity will contain an optimistic locking version number
 *
 * @author Simon Cutts
 */
@RestController
@EnableWebMvc
@RequestMapping("/customer/v1/command")
public class CommandController {

    private static final Logger LOG = Logger.getLogger(CommandController.class);

    private DynamoDBMapper mapper = DynamoDBAdapter.getInstance().getDynamoDBMapper();

    /**
     * Add a person customer
     * Sends the the PersonCustomer created event to the command
     *
     * @param customer PersonCustomer to add
     * @return 200
     */
    @RequestMapping(path = "/createPerson", method = RequestMethod.POST)
    public ResponseEntity<Customer> addPersonCustomer(@RequestBody PersonCustomer customer) {

        CustomerCreateCommand command = new CustomerCreateCommand(new PersonCustomerCreatedEvent(customer),
                mapper);
        Customer cust = command.persist();
        return ResponseEntity.ok(cust);
    }

    /**
     * Amend a person customer
     * Sends the the PersonCustomer amended event to the command
     *
     * @param customer PersonCustomer to add
     * @return 200
     */
    @RequestMapping(path = "/amendPerson", method = RequestMethod.POST)
    public ResponseEntity<Customer> updatePersonCustomer(@RequestBody PersonCustomer customer) {

        try {
            CustomerUpdateCommand command = new CustomerUpdateCommand(new PersonCustomerUpdatedEvent(customer),
                    mapper);
            Customer cust = command.persist();
            return ResponseEntity.ok(cust);
        } catch (Exception e) {
            LOG.info("Unable to find Customer Id " + customer.getCustomerId());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add a organisation customer
     * Sends the the OrganisationCustomer created event to the command
     *
     * @param customer OrganisationCustomer to add
     * @return 200
     */
    @RequestMapping(path = "/createOrganisation", method = RequestMethod.POST)
    public ResponseEntity<Customer> addOrganisationCustomer(@RequestBody OrganisationCustomer customer) {

        CustomerCreateCommand command = new CustomerCreateCommand(new OrganisationCustomerCreatedEvent(customer),
                mapper);
        Customer cust = command.persist();
        return ResponseEntity.ok(cust);
    }

    /**
     * Rebuild a customer from RebuildRequest
     *
     * @param rebuildRequest RebuildRequest to add
     * @return 200
     */
    @RequestMapping(path = "/rebuild", method = RequestMethod.POST)
    public ResponseEntity<Customer> rebuild(@RequestBody RebuildRequest rebuildRequest) {

        try {
            CustomerRebuildCommand command = new CustomerRebuildCommand(mapper, rebuildRequest.getCustomerId(),
                    ZonedDateTime.parse(rebuildRequest.getTimestamp()));
            Customer cust = command.rebuild();
            return ResponseEntity.ok(cust);
        } catch (CustomerNotFoundException e) {
            LOG.info("Unable to find Customer Id " + rebuildRequest.getCustomerId() + " for this date " + rebuildRequest.getTimestamp());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a customer
     *
     * @param customerId CustomerId to delete
     * @return 200
     */
    @RequestMapping(path = "/delete/{customerId}", method = RequestMethod.POST)
    public ResponseEntity delete(@PathVariable("customerId") String customerId) {

        try {
            CustomerDeleteEvent deleteEvent = new CustomerDeleteEvent(customerId);
            CustomerDeleteCommand deleteCommand = new CustomerDeleteCommand(deleteEvent, mapper);
            deleteCommand.persist();
            return ResponseEntity.ok().build();
        } catch (CustomerNotFoundException e) {
            LOG.info("Unable to find Customer Id " + customerId);
            return ResponseEntity.badRequest().build();
        }
    }
}
