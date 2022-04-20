package com.javislaptop.correlation.datastore

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI


@Configuration
class DynamoDbConfig {

    @Bean
    fun initialiseDb() : DynamoDbClient {
        val dynamoDbClient = DynamoDbClient
            .builder()
            .region(Region.EU_WEST_1)
            .build()
        val tableExist = dynamoDbClient.listTables().tableNames().any { "StockEOD".equals(it) }
        if (!tableExist) {
            val createTableRequest = CreateTableRequest.builder()
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("symbol")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("date")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("symbol")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("date")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(5L)
                        .build()
                )
                .tableName("StockEOD")
                .build()
            dynamoDbClient.createTable(createTableRequest)
        }

        val optionsTableExist = dynamoDbClient.listTables().tableNames().any { "Options".equals(it) }
        if (!optionsTableExist) {
            val createTableRequest = CreateTableRequest.builder()
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("optionSymbol")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("date")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("optionSymbol")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("date")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(5L)
                        .build()
                )
                .tableName("Options")
                .build()
            dynamoDbClient.createTable(createTableRequest)
        }
        return dynamoDbClient
    }

}