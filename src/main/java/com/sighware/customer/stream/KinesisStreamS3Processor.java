package com.sighware.customer.stream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;

/**
 * Example Kinesis stream client that takes an item off the stream and writes it as a CustomerEvent to S3.
 * Normally this class would be in a separate project as it is a client of the event stream. Only included
 * here for illustration purposes
 */
public class KinesisStreamS3Processor implements
        RequestHandler<KinesisEvent, String> {

    private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private final EventWriter writer = new EventWriter();

    public String handleRequest(KinesisEvent input, Context context) {

        for (KinesisEvent.KinesisEventRecord record : input.getRecords()) {
            String json = new String(record.getKinesis().getData().array());
            System.out.println(json);
            writer.write(BUCKET_NAME, json);
        }
        return "Ok";
    }
}