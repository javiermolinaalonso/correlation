package com.javislaptop.correlation.dataloader.options

import com.assets.options.entities.Option
import com.assets.options.entities.OptionBuilder
import com.javislaptop.correlation.dataloader.deltaneutral.DeltaNeutralOption
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import org.springframework.stereotype.Service

@Service
class DeltaNeutralConverter(
    val stockLoader: DataLoaderDynamoDb
) {

    fun convert(item: DeltaNeutralOption): Option {
        val builder = OptionBuilder.create(item.underlyingSymbol, item.underlyingPrice)
            .withExpirationAt(item.expiration)
            .withCurrentDate(item.date)
            .withBidAsk(item.bid, item.ask)
            .withRiskFree(stockLoader.loadRiskFree(item.date).div(100))
            .withStrikePrice(item.strike)
            .withOptionSymbol(item.optionSymbol)
        if (item.type == "call") {
            return builder.buildCall()
        } else {
            return builder.buildPut()
        }
    }

    fun convert(input: List<DeltaNeutralOption>) =
        input.map {
            convert(it)
        }.toList()

}