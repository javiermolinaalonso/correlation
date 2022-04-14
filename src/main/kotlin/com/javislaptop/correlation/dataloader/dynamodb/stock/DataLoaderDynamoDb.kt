package com.javislaptop.correlation.dataloader.dynamodb.stock

import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderResponse
import com.javislaptop.correlation.datastore.DynamoDbProperties
import com.javislaptop.correlation.model.StockEOD
import org.springframework.stereotype.Service
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator
import software.amazon.awssdk.services.dynamodb.model.Condition
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.LocalDate

@Service
class DataLoaderDynamoDb(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDbProperties: DynamoDbProperties
) {
    fun loadData(dataLoaderRequest: DataLoaderRequestEOD) : List<StockEOD> {
        val request = QueryRequest.builder()
            .tableName(dynamoDbProperties.table)
            .keyConditionExpression("#symbol = :symbolValue")
            .expressionAttributeNames(mapOf(Pair("#symbol", "symbol")))
            .expressionAttributeValues(mapOf(Pair(":symbolValue", AttributeValue.fromS(dataLoaderRequest.symbol))))
            .build()
        val response = dynamoDbClient.query(request)

        return response.items().map {
            StockEOD(
                LocalDate.parse(it["date"]!!.s()),
                it["open"]!!.n().toDouble(),
                it["high"]!!.n().toDouble(),
                it["low"]!!.n().toDouble(),
                it["close"]!!.n().toDouble(),
                0.0,
                it["volume"]!!.n().toDouble()
            )
        }.toList()
    }

}