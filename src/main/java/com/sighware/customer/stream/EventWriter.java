package com.sighware.customer.stream;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sighware.customer.event.CustomerEvent;
import com.sighware.customer.util.JsonConverter;

/**
 * Writes JSON representations of CustomerEvents to an S3 bucket
 */
public class EventWriter {

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    /**
     * Write CustomerEvent to the S3 bucket
     * @param bucket
     * @param event
     * @return
     */
    public String write(String bucket, CustomerEvent event) {
        String key = event.getEventName() + "-" + event.getCreateTime() + "-" + event.getCustomerId();
        s3.putObject(bucket, key, JsonConverter.toJson(event));
        return "Ok";
    }

    /**
     * Writes JSON representations of CustomerEvents to an S3 bucket
     *
     * @param bucket
     * @param json
     * @return
     */
    public String write(String bucket, String json) {
        return write(bucket, (CustomerEvent) JsonConverter.toObject(json, CustomerEvent.class));
    }
}
