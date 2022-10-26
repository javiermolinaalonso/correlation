package com.javislaptop.correlation.dataloader.options

import com.assets.options.entities.Option
import com.assets.options.entities.OptionBuilder
import com.javislaptop.correlation.dataloader.eodhistoricaldata.options.OptionEOD
import com.javislaptop.correlation.datastore.DynamoDbProperties
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.LocalDate

@Service
//@Primary
class OptionsLoaderDynamoDb(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDbProperties: DynamoDbProperties
) : OptionsLoader {

    override fun load(request: OptionsLoaderRequest): List<Option> {
        val dbRequest = QueryRequest.builder()
            .tableName(dynamoDbProperties.optionsTable)
            .indexName("symbol-date-idx")
            .keyConditionExpression("#symbol = :symbolValue AND #date = :date")
            .expressionAttributeNames(mapOf(Pair("#symbol", "symbol"), Pair("#date", "date")))
            .expressionAttributeValues(
                mapOf(
                    Pair(":symbolValue", AttributeValue.fromS(request.symbol)),
                    Pair(":date", AttributeValue.fromS(request.date.toString()))
                )
            )
            .build()
        return executeQuery(dbRequest)
    }

    override fun loadUnderlyingPrice(request: OptionsLoaderRequest) = load(request).firstOrNull()?.currentPrice?.toDouble()

    fun loadByOptionSymbol(request: OptionsLoaderRequest) : List<Option> {
        val dbRequest = QueryRequest.builder()
            .tableName(dynamoDbProperties.optionsTable)
            .keyConditionExpression("#optionSymbol = :symbolValue AND #date = :date")
            .expressionAttributeNames(mapOf(Pair("#optionSymbol", "optionSymbol"), Pair("#date", "date")))
            .expressionAttributeValues(
                mapOf(
                    Pair(":symbolValue", AttributeValue.fromS(request.symbol)),
                    Pair(":date", AttributeValue.fromS(request.date.toString()))
                )
            )
            .build()
        return executeQuery(dbRequest)
    }

    fun loadOptionAtExpiration(option: Option) : Option? {
        val options = loadByOptionSymbol(
            OptionsLoaderRequest(
                option.optionSymbol,
                option.expirationDate
            )
        )
        if (options.isNotEmpty()) {
            return options[0]
        } else {
            return null
        }
    }

    private fun executeQuery(dbRequest: QueryRequest?) =
        dynamoDbClient.query(dbRequest).items()
            .map {
                val builder = OptionBuilder.create(
                    it["symbol"]!!.s(),
                    it["underlyingPrice"]!!.n().toDouble()
                )
                    .withStrikePrice(it["strike"]!!.n().toDouble())
                    .withOptionSymbol(it["optionSymbol"]!!.s())
                    .withCurrentDate(LocalDate.parse(it["date"]!!.s()))
                    .withBidAsk(it["bid"]!!.n().toDouble(), it["ask"]!!.n().toDouble())
                    .withExpirationAt(LocalDate.parse(it["expiryDate"]!!.s()))
                    .withIV(it["iv"]!!.n().toDouble())
                if (it["type"]!!.s().equals("C")) {
                    builder.buildCall()
                } else {
                    builder.buildPut()
                }
            }.toList()
}

data class OptionsLoaderRequest(val symbol: String, val date: LocalDate)
