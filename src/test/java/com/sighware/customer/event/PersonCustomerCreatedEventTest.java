package com.sighware.customer.event;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sighware.customer.command.CustomerCreateCommand;
import com.sighware.customer.model.CustomerBuilder;
import com.sighware.customer.model.PersonCustomer;
import com.sighware.customer.util.DynamoDBAdapter;
import org.junit.Assert;
import org.junit.Test;

public class PersonCustomerCreatedEventTest {

    private static DynamoDBAdapter adapter = DynamoDBAdapter.getInstance();

    @Test
    public void testPersonCreatedEvent() throws Exception {

        // create the customer object for post
        PersonCustomer customer = CustomerBuilder.buildPerson();

        CustomerEvent event = new PersonCustomerCreatedEvent(customer);
        CustomerCreateCommand command = new CustomerCreateCommand(event, adapter.getDynamoDBMapper());
        command.persist();

        // now retrieve the saved item
        PersonCustomer cust = adapter.getDynamoDBMapper().load(PersonCustomer.class, customer.getCustomerId());
        String result = new ObjectMapper().writeValueAsString(cust);
//        System.out.println(result);

        Assert.assertTrue(result.startsWith("{\"customerId\":\"" + customer.getCustomerId() +
                "\",\"customerName\":{\"title\":\"Mr\",\"foreNames\":\"Owain\",\"surname\":\"Glyn Dŵr\"}"));
        Assert.assertTrue(result.endsWith("\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\"" +
                ",\"postalCode\":\"SA14FR\"}},\"version\":1}"));
    }
}
