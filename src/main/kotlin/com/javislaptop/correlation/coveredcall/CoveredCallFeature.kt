package com.javislaptop.correlation.coveredcall

import com.assets.options.entities.Option
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.correlation.dataloader.options.OptionsLoaderDynamoDb
import com.javislaptop.correlation.dataloader.options.OptionsLoaderRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period
import kotlin.math.abs

@Service
class CoveredCallFeature(
    val stockLoader: DataLoaderDynamoDb,
    val optionsLoader: OptionsLoaderDynamoDb
) {

    fun analyse(request: CoveredCallFeatureRequest): Double {
        val stockValues = stockLoader.loadData(DataLoaderRequestDynamoDb(request.symbol, request.from, request.to))
            .associateBy { it.date }
        val options = mutableListOf<Option>()
        request.from.datesUntil(request.to, Period.ofDays(request.frequency.days)).forEach { date ->
            val closestStrike = stockValues[date]?.close?.times(1 + request.strikeDistance)
            val closestExpiry = date.plus(request.temporalDistance)
            val option = optionsLoader
                .load(OptionsLoaderRequest(request.symbol, date))
                .filter { it.isCall }
                .filter { it.expirationDate.isAfter(closestExpiry) }
                .sortedBy { it.expirationDate }
                .minByOrNull { abs(it.strikePrice.toDouble() - closestStrike!!) }
            if (option != null) {
                options.add(option)
            }
        }

        var totalprofit = 0.0
        options.forEach {
            val loadByOptionSymbol =
                optionsLoader.loadByOptionSymbol(OptionsLoaderRequest(it.optionSymbol, it.expirationDate))
            if (loadByOptionSymbol.size == 1) {
                val finalPrice = loadByOptionSymbol[0].bid
                val initialPrice = it.bid
                val profit = initialPrice.subtract(finalPrice).multiply(BigDecimal(100))
                val stockProfit = loadByOptionSymbol[0].currentPrice.subtract(it.currentPrice).multiply(BigDecimal(100))
                println(it)
                println(loadByOptionSymbol[0])
                println("Premium profit: $profit. Stock profit: $stockProfit")
                totalprofit += profit.toDouble() + stockProfit.toDouble()
            }
        }
        return totalprofit
    }
}

data class CoveredCallFeatureRequest(
    val symbol: String,
    val from: LocalDate,
    val to: LocalDate,
    val strikeDistance: Double,
    val temporalDistance: Period,
    val frequency: Period
)
