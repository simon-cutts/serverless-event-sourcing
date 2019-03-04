package com.sighware.customer.controller;

import com.sighware.customer.model.Customer;
import com.sighware.customer.model.CustomerBuilder;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.sighware.customer.model.RebuildRequest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class CommandControllerTest {

    @Test
    public void testCreateCustomer() {
        CommandController controller = new CommandController();
        ResponseEntity<Customer> resp = controller.addPersonCustomer(CustomerBuilder.buildPerson());

        // Check customer is persisted and has a version of 1
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(new Long(1), resp.getBody().getVersion());
    }

    @Test
    public void testRebuild() {
        CommandController controller = new CommandController();
        ResponseEntity<Customer> resp = controller.addPersonCustomer(CustomerBuilder.buildPerson());

        // Check customer is persisted and has a version of 1
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(new Long(1), resp.getBody().getVersion());

        // Get a rebuilt view of customer
        Customer c = resp.getBody();
        RebuildRequest r = new RebuildRequest(c.getCustomerId(), ZonedDateTime.now(ZoneOffset.UTC));

        resp = controller.rebuild(r);

        // Check customer is found and has a version of 1
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(new Long(1), resp.getBody().getVersion());

        // Same customer but date is too early
        ZonedDateTime inThePast = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");
        r = new RebuildRequest(c.getCustomerId(), inThePast);
        resp = controller.rebuild(r);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

    }

    @Test
    public void testDelete() {
        CommandController controller = new CommandController();
        ResponseEntity<Customer> resp = controller.addPersonCustomer(CustomerBuilder.buildPerson());

        // Check customer is persisted and has a version of 1
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(new Long(1), resp.getBody().getVersion());

        // Get a rebuilt view of customer
        Customer c = resp.getBody();
        resp = controller.delete(c.getCustomerId());

        // Check 200 returned
        assertEquals(HttpStatus.OK, resp.getStatusCode());

    }
}
