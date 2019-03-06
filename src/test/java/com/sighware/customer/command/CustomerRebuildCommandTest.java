package com.sighware.customer.command;


import com.sighware.customer.error.CustomerNotFoundException;
import com.sighware.customer.event.*;
import com.sighware.customer.model.Customer;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.OrganisationCustomer;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.util.DynamoDBAdapter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * Test the rebuilding of Person and Organisation customers from the event stream. Tests full rebuild
 * of a customer and a snapshot of a customer from a point in time
 */
public class CustomerRebuildCommandTest {

    private static final DynamoDBAdapter adapter = DynamoDBAdapter.getInstance();
    private static PersonCustomer updateCust;
    private static String timestamp;
    private static OrganisationCustomer organisationCustomer;
    private static String eventCreateTime;

    @BeforeClass
    public static void setUp() throws CustomerNotFoundException {
        // create the customer object for post
        PersonCustomer customer = CustomerBuilder.buildPerson();

        CustomerEvent event = new PersonCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (PersonCustomer) command.persist();

        // now update the customer with Bill
        updateCust.getCustomerName().setForeNames("Bill");
        event = new PersonCustomerUpdatedEvent(updateCust);
        timestamp = event.getCreateTime();
        CustomerUpdateCommand updatecommand = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (PersonCustomer) updatecommand.persist();

        // now update the customer again with Bong
        updateCust.getCustomerName().setSurname("Bong");
        event = new PersonCustomerUpdatedEvent(updateCust);
        updatecommand = new CustomerUpdateCommand(event, adapter.getDynamoDBMapper());
        updateCust = (PersonCustomer) updatecommand.persist();

        // create the org customer object for post
        OrganisationCustomer orgCustomer = CustomerBuilder.buildOrganistion();

        CustomerEvent customerEvent = new OrganisationCustomerCreatedEvent(orgCustomer);
        command = new CustomerCreateCommand(customerEvent, adapter.getDynamoDBMapper());
        organisationCustomer = (OrganisationCustomer) command.persist();

        // now update the orgCustomer with 2222222222222222
        organisationCustomer.setOrganisationName("2222222222222222");
        customerEvent = new OrganisationCustomerUpdatedEvent(organisationCustomer);
        eventCreateTime = customerEvent.getCreateTime();
        updatecommand = new CustomerUpdateCommand(customerEvent, adapter.getDynamoDBMapper());
        organisationCustomer = (OrganisationCustomer) updatecommand.persist();

        // now update the orgCustomer again with 333333333333333
        organisationCustomer.setOrganisationName("333333333333333");
        customerEvent = new OrganisationCustomerUpdatedEvent(organisationCustomer);
        updatecommand = new CustomerUpdateCommand(customerEvent, adapter.getDynamoDBMapper());
        organisationCustomer = (OrganisationCustomer) updatecommand.persist();
    }

    @Test
    public void testPersonFullRebuild() throws Exception {

        // Test full rebuild for Bill Bong
        CustomerRebuildCommand rebuild = new CustomerRebuildCommand(updateCust.getCustomerId(),
                adapter.getDynamoDBMapper());
        Customer c = rebuild.rebuild();
        assertEquals("Bill", c.getCustomerName().getForeNames());
        assertEquals("Bong", c.getCustomerName().getSurname());
        assertEquals(new Long("3"), c.getVersion());
    }

    @Test
    public void testPersonPointInTimeRebuild() throws Exception {

        // Now test from a point in time rebuild for Bill Glyn Dŵr
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp);
        CustomerRebuildCommand rebuild = new CustomerRebuildCommand(adapter.getDynamoDBMapper(),
                updateCust.getCustomerId(), zonedDateTime);
        Customer c = rebuild.rebuild();
        assertEquals("Bill", c.getCustomerName().getForeNames());
        assertEquals("Glyn Dŵr", c.getCustomerName().getSurname());
        assertEquals(new Long("2"), c.getVersion());
    }

    @Test
    public void testOrgFullRebuild() throws Exception {

        // Test full rebuild for 333333333333333
        CustomerRebuildCommand rebuild = new CustomerRebuildCommand(organisationCustomer.getCustomerId(),
                adapter.getDynamoDBMapper());
        Customer c = rebuild.rebuild();
        assertEquals("333333333333333", c.getOrganisationName());
        assertEquals(new Long("3"), c.getVersion());
    }

    @Test
    public void testOrgPointInTimeRebuild() throws Exception {

        // Now test from a point in time rebuild for 2222222222222222
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(eventCreateTime);
        CustomerRebuildCommand rebuild = new CustomerRebuildCommand(adapter.getDynamoDBMapper(),
                organisationCustomer.getCustomerId(), zonedDateTime);
        Customer c = rebuild.rebuild();
        assertEquals("2222222222222222", c.getOrganisationName());
        assertEquals(new Long("2"), c.getVersion());
    }

    @Test
    public void testDateToEarly() {

        ZonedDateTime inThePast = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");

        Customer c = null;
        try {
            CustomerRebuildCommand rebuild = new CustomerRebuildCommand(adapter.getDynamoDBMapper(),
                    updateCust.getCustomerId(), inThePast);
            c = rebuild.rebuild();
            fail("Should not have found a customer for that date");
        } catch (CustomerNotFoundException e) {
            assertNull(c);
        }
    }
}
