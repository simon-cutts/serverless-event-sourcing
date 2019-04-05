package com.sighware.customer.stream;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.util.JsonConverter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Processes new image events from a DynamoDB stream on the CustomerEvent table. Reads from the DynamoDB stream, converts
 * data to a json CustomerEvent and then writes the json to the kinesis stream
 */
public class DynamoDbStreamProcessor implements
        RequestHandler<DynamodbEvent, String> {

    private static final String KINESIS_STREAM_NAME = System.getenv("KINESIS_STREAM_NAME");
    private final AmazonKinesis kinesis = AmazonKinesisClientBuilder.defaultClient();

    public String handleRequest(DynamodbEvent ddbEvent, Context context) {

        for (DynamodbStreamRecord record : ddbEvent.getRecords()) {

            // Only process a new image
            Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
            if (newImage != null) {

                // Load the stream data to be converted into json
                List<Map<String, AttributeValue>> listOfMaps = new ArrayList<>();
                listOfMaps.add(newImage);
                List<Item> itemList = ItemUtils.toItemList(listOfMaps);

                for (Item item : itemList) {
                    String json = item.toJSON();

                    System.out.println(json);

                    // Now write customer event to kinesis stream
                    CustomerEvent event = (CustomerEvent) JsonConverter.toObject(json, CustomerEvent.class);
                    ByteBuffer data = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
                    PutRecordResult r = kinesis.putRecord(KINESIS_STREAM_NAME, data,
                            event.getCustomerId() + "-" + event.getCreateTime());
                }
            }
        }
        return "Ok";
    }
}