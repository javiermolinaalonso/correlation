package com.javislaptop.correlation.datastore

import com.assets.options.entities.Option
import com.javislaptop.correlation.dataloader.deltaneutral.DataLoaderCsv
import com.javislaptop.correlation.dataloader.options.DeltaNeutralConverter
import com.javislaptop.correlation.model.StockEOD
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutRequest
import software.amazon.awssdk.services.dynamodb.model.WriteRequest
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class StoreOptionsToDynamoDB(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDbProperties: DynamoDbProperties,
    private val loader: DataLoaderCsv,
    private val converter: DeltaNeutralConverter
) {

    fun store() {
        loader.listFiles()
            .forEach { file ->
                println(LocalDateTime.now().toString() + " Processing $file")
                val options = loader.loadData(file).map { converter.convert(it) }

                val map = mutableMapOf<String, List<WriteRequest>>()
                val list = mutableListOf<WriteRequest>()
                map[dynamoDbProperties.optionsTable] = list
                options.forEachIndexed { i, v ->
                    list.add(
                        WriteRequest.builder()
                            .putRequest(
                                PutRequest.builder()
                                    .item(convert(v))
                                    .build()
                            ).build()
                    )
                    if (i > 0 && i % 20 == 0) {
                        try {
                            dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder().requestItems(map).build())
                        } catch (_ : Exception) {
                            println()
                        }
                        list.clear()
                    }
                }
                if ((map[dynamoDbProperties.optionsTable]?.size ?: 0) > 0) {
                    dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder().requestItems(map).build())
                }
            }


    }

    private fun convert(option: Option): MutableMap<String, AttributeValue>? {
        val r = mutableMapOf<String, AttributeValue>()
        r["optionSymbol"] = AttributeValue.fromS(option.optionSymbol)
        r["date"] = AttributeValue.fromS(option.currentDate.toString())
        r["symbol"] = AttributeValue.fromS(option.ticker + ".US")
        r["underlyingPrice"] = AttributeValue.fromN(option.currentPrice.setScale(2, RoundingMode.HALF_UP).toString())
        r["type"] = AttributeValue.fromS(if (option.isCall) "C" else "P")
        r["expiryDate"] = AttributeValue.fromS(option.expirationDate.toString())
        r["strike"] = AttributeValue.fromN(option.strikePrice.setScale(2, RoundingMode.HALF_UP).toString())
        r["bid"] = AttributeValue.fromN(option.bid.setScale(2, RoundingMode.HALF_UP).toString())
        r["ask"] = AttributeValue.fromN(option.ask.setScale(2, RoundingMode.HALF_UP).toString())
        r["iv"] = AttributeValue.fromN(String.format("%.4f", option.impliedVolatility).replace("NaN", "0.0000"))
        r["delta"] = AttributeValue.fromN(String.format("%.4f", option.greeks.delta).replace("NaN", "0.0000"))
        r["vega"] = AttributeValue.fromN(String.format("%.4f", option.greeks.vega).replace("NaN", "0.0000"))
        r["theta"] = AttributeValue.fromN(String.format("%.4f", option.greeks.theta).replace("NaN", "0.0000"))
        r["gamma"] = AttributeValue.fromN(String.format("%.4f", option.greeks.gamma).replace("NaN", "0.0000"))
        r["rho"] = AttributeValue.fromN(String.format("%.4f", option.greeks.rho).replace("NaN", "0.0000"))
        return r
    }

}