package com.javislaptop.correlation.dataloader.dynamodb.stock

import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.datastore.DynamoDbProperties
import com.javislaptop.correlation.model.StockEOD
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.LocalDate

@Service
class DataLoaderDynamoDb(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDbProperties: DynamoDbProperties
) {

    private val cachedRiskFree = mutableMapOf<LocalDate, Double>()

    fun loadRiskFree(day : LocalDate) =
        try {
            cachedRiskFree.getOrPut(day) {
                loadData(DataLoaderRequestDynamoDb("US10Y.GBOND", day.minusDays(7L), day))
                    .map { it.close }
                    .last()
            }
        }catch (it : java.util.NoSuchElementException) {
            println(day)
            throw it
        }


    fun loadData(dataLoaderRequest: DataLoaderRequestDynamoDb) : List<StockEOD> {
        var symbol = dataLoaderRequest.symbol
        if (!dataLoaderRequest.symbol.endsWith("US")) {
            symbol = symbol + ".US"
        }
        val request = QueryRequest.builder()
            .tableName(dynamoDbProperties.table)
            .keyConditionExpression("#symbol = :symbolValue AND #date BETWEEN :from AND :to")
            .expressionAttributeNames(mapOf(Pair("#symbol", "symbol"), Pair("#date", "date")))
            .expressionAttributeValues(mapOf(
                Pair(":symbolValue", AttributeValue.fromS(symbol)),
                Pair(":from", AttributeValue.fromS(dataLoaderRequest.from.toString())),
                Pair(":to", AttributeValue.fromS(dataLoaderRequest.to.toString()))
            ))
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

data class DataLoaderRequestDynamoDb (val symbol: String, val from: LocalDate, val to: LocalDate)
