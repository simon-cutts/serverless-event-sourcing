package com.sighware.customer.stream;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Processes new image events from a DynamoDB stream on the CustomerEvent table
 */
public class DynamoDbStreamProcessor implements
        RequestHandler<DynamodbEvent, String> {

    public String handleRequest(DynamodbEvent ddbEvent, Context context) {
        for (DynamodbStreamRecord record : ddbEvent.getRecords()){

            Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
            List<Map<String, AttributeValue>> listOfMaps = new ArrayList<>();
            listOfMaps.add(newImage);
            List<Item> itemList = ItemUtils.toItemList(listOfMaps);
            for (Item item : itemList) {
                String json = item.toJSON();
                System.out.println(json);
            }
        }
        return "Successfully processed " + ddbEvent.getRecords().size() + " records.";
    }
}