package com.javislaptop.correlation.datastore

import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.model.StockEOD
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutRequest
import software.amazon.awssdk.services.dynamodb.model.WriteRequest

@Service
class StoreStockEodToDynamoDB (
    private val loader : DataLoaderEOD,
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDbProperties: DynamoDbProperties
    ){

    fun store(dataLoaderRequest: DataLoaderRequestEOD) {
        val data = loader.loadData(dataLoaderRequest)
        val r = mutableMapOf<String, List<WriteRequest>>()
        val list = mutableListOf<WriteRequest>()
        r[dynamoDbProperties.table] = list
        data.forEachIndexed { i, v ->
            list.add(WriteRequest.builder()
                .putRequest(
                    PutRequest.builder()
                        .item(convert(v, dataLoaderRequest.symbol))
                        .build()
                ).build()
            )
            if (i > 0 && i % 20 == 0) {
                dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder().requestItems(r).build())
                list.clear()
            }
//            dynamoDbClient.putItem(
//                PutItemRequest.builder()
//                    .tableName(dynamoDbProperties.table)
//                    .item(convert(it, dataLoaderRequest.symbol))
//                    .build()
//            )
        }
        r[dynamoDbProperties.table] = list

    }

    private fun convert(stockEOD: StockEOD, symbol : String): MutableMap<String, AttributeValue> {
        val r = mutableMapOf<String, AttributeValue>()
        r["date"] = AttributeValue.fromS(stockEOD.date.toString())
        r["symbol"] = AttributeValue.fromS(symbol)
        r["open"] = AttributeValue.fromN(stockEOD.open.toString())
        r["close"] = AttributeValue.fromN(stockEOD.close.toString())
        r["high"] = AttributeValue.fromN(stockEOD.high.toString())
        r["low"] = AttributeValue.fromN(stockEOD.low.toString())
        r["volume"] = AttributeValue.fromN(stockEOD.volume.toString())
        return r;
    }
}