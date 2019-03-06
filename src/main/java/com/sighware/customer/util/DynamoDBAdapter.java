package com.sighware.customer.util;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDBAdapter {

    public static final String CUSTOMER_TABLE = "Customer";
    public static final String EVENT_TABLE = "CustomerEvent";

    private static DynamoDBAdapter self = null;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;
    private final DynamoDB dynamoDB;


    private DynamoDBAdapter() {
        // create the client
        client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .build();
        dynamoDB = new DynamoDB(client);
        mapper = new DynamoDBMapper(client);
    }

    private DynamoDBAdapter(AwsClientBuilder.EndpointConfiguration config) {
        // create the client
        client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(config)
                .build();
        dynamoDB = new DynamoDB(client);
        mapper = new DynamoDBMapper(client);
    }

    public static DynamoDBAdapter getInstance() {
        if (self == null)
            self = new DynamoDBAdapter();
        return self;
    }

    public static DynamoDBAdapter getInstance(AwsClientBuilder.EndpointConfiguration config) {
        if (self == null)
            self = new DynamoDBAdapter();
        return self;
    }

    public AmazonDynamoDB getDbClient() {
        return client;
    }

    public DynamoDBMapper getDynamoDBMapper() {
        return mapper;
    }

    public DynamoDB getDynamoDB() {
        return dynamoDB;
    }
}
