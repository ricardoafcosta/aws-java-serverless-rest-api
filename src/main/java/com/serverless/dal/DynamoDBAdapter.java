package com.serverless.dal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

/**
 * Creates an adapter whose responsibility will be to manage the connection to the specifed DynamoDB table
 * using configuration, like the AWS region where the table will be deployed. The DynamoDB adapter class
 * is a singleton that instantiates a AmazonDynamoDB client and a AWS DBMapper class.
 */
public class DynamoDBAdapter {

    private static DynamoDBAdapter DB_ADAPTER = null;
    private final AmazonDynamoDB client;
    private DynamoDBMapper mapper;
    private static String region = System.getenv("AWS_REGION");

    private DynamoDBAdapter() {
        // create the client
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    public static DynamoDBAdapter getInstance() {
        if (DB_ADAPTER == null)
            DB_ADAPTER = new DynamoDBAdapter();

        return DB_ADAPTER;
    }

    public AmazonDynamoDB getDbClient() {
        return this.client;
    }

    public DynamoDBMapper createDbMapper(DynamoDBMapperConfig mapperConfig) {
        // create the mapper with the mapper config
        if (this.client != null)
            mapper = new DynamoDBMapper(this.client, mapperConfig);

        return this.mapper;
    }
}
