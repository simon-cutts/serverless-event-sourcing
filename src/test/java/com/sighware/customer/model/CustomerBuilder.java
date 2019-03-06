package com.sighware.customer.model;

public class CustomerBuilder {

    public static PersonCustomer buildPerson() {

        Address addr = new Address("addressLine1", "addressLine2", "addressLine3"
                , null, null, "SA14FR");

        CustomerAddress custAddr = new CustomerAddress("STRUCTURED",
                addr,
                "SA14FR");

        // create the name
        CustomerName name = new CustomerName("Mr",
                "Owain",
                "Glyn Dŵr");

        // create the customer object for post
        return new PersonCustomer(null,
                name,
                custAddr);
    }

    public static OrganisationCustomer buildOrganistion() {

        Address addr = new Address("addressLine1", "addressLine2", "addressLine3"
                , null, null, "SA14FR");

        CustomerAddress custAddr = new CustomerAddress("STRUCTURED",
                addr,
                "SA14FR");

        // create the customer object for post

        return new OrganisationCustomer(null,
                "My Organisation",
                custAddr);
    }

}
