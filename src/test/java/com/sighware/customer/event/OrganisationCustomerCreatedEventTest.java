package com.sighware.customer.event;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sighware.customer.command.CustomerCreateCommand;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.OrganisationCustomer;
import com.sighware.customer.util.DynamoDBAdapter;
import org.junit.Assert;
import org.junit.Test;

public class OrganisationCustomerCreatedEventTest {

    private static DynamoDBAdapter adapter = DynamoDBAdapter.getInstance();

    @Test
    public void testOrgCreatedEvent() throws Exception {

        // create the customer object for post
        OrganisationCustomer customer = CustomerBuilder.buildOrganistion();

        CustomerEvent event = new OrganisationCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        command.persist();

        // now retrieve the saved item
        OrganisationCustomer cust = adapter.getDynamoDBMapper().load(OrganisationCustomer.class, customer.getCustomerId());
        String result = new ObjectMapper().writeValueAsString(cust);
//        System.out.println(result);

        Assert.assertTrue(result.startsWith("{\"customerId\":\"" + customer.getCustomerId() + "\",\"organisationName\":\"My Organisation\""));
        Assert.assertTrue(result.endsWith("\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"postalCode\":\"SA14FR\"}},\"version\":1}"));
    }
}
