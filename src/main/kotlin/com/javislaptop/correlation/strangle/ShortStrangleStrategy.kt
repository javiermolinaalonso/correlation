package com.javislaptop.correlation.strangle

import com.assets.options.entities.Option
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.correlation.dataloader.options.OptionsLoader
import com.javislaptop.correlation.dataloader.options.OptionsLoaderDynamoDb
import com.javislaptop.correlation.dataloader.options.OptionsLoaderRequest
import com.javislaptop.correlation.model.IronButterflySpread
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt

@Service
class ShortStrangleStrategy(
    val stockLoader: DataLoaderDynamoDb,
    val optionsLoader: OptionsLoader
) {
    fun analyse(request: StrangleRequest): StrategyResponse {
        val profits = mutableListOf<Double>()
        val ivs = mutableListOf<Double>()
        var executions = 0
        for (date in request.from.datesUntil(request.to, Period.ofDays(request.frequency.days))) {
            val peakStrike = optionsLoader.loadUnderlyingPrice(OptionsLoaderRequest(request.symbol, date))?.roundToInt()
            if (peakStrike != null) {
                val width = max((peakStrike * request.width).roundToInt(), 1)
                val lowStrike = peakStrike - width
                val highStrike = peakStrike + width
                val closestExpiry = date.plus(request.temporalDistance)

                val spread = buildSpread(request.symbol, date, lowStrike, peakStrike, highStrike, closestExpiry)
                val expiryDate = spread?.expiryDate()?.minusDays(1)
                if (expiryDate != null) {
//                    val expirationSpread =
//                        buildSpread(request.symbol, expiryDate, lowStrike, peakStrike, highStrike, expiryDate)
                    val expirationPrice =
                        optionsLoader.loadUnderlyingPrice(OptionsLoaderRequest(request.symbol, expiryDate))

                    if (expirationPrice != null) {
                        val risk = spread.risk()
                        val profit = spread.premium().subtract(spread.atmPut.strikePrice.minus(BigDecimal(expirationPrice)).abs().min(spread.atmPut.strikePrice - spread.oomPut.strikePrice))
                        val profitPerc = profit.toDouble() * 100 / risk
                        val stockValues =
                            stockLoader.loadData(DataLoaderRequestDynamoDb(request.symbol, date.minusDays(120), date))
                                .map { it.close }.toDoubleArray()

                        val stdDev = StandardDeviation().evaluate(stockValues)
                        val avgPrice = stockValues.average()
                        val pctStdDev = stdDev * 100 / avgPrice
                        if (pctStdDev < 20) {
                            println(
                                String.format(
                                    "%s, %.2f%%, %.2f, %.4f",
                                    request.symbol,
                                    profitPerc,
                                    spread.iv(),
                                    pctStdDev
                                )
                            )
                            profits.add(profitPerc)
                        }
                    }
                }
            }
        }

        return StrategyResponse(profits.average(), ivs.average(), executions)
    }

    private fun buildSpread(
        symbol: String,
        date: LocalDate,
        lowStrike: Int,
        peakStrike: Int,
        highStrike: Int,
        expiry: LocalDate
    ): IronButterflySpread? {
        val dailyOptions = optionsLoader.load(OptionsLoaderRequest(symbol, date))
            .filter { !it.expirationDate.isBefore(expiry) }
            .sortedBy { it.expirationDate }

        val atmCallOption = getOptionAt(dailyOptions, peakStrike.toDouble()) { op -> op.isCall }
        val atmPutOption = getOptionAt(dailyOptions, peakStrike.toDouble()) { op -> !op.isCall }
        val lowStrikePut = getOptionAt(dailyOptions, lowStrike.toDouble()) { op -> !op.isCall }
        val highStrikeCall = getOptionAt(dailyOptions, highStrike.toDouble()) { op -> op.isCall }

        if (atmCallOption != null && atmPutOption != null && lowStrikePut != null && highStrikeCall != null) {
            return IronButterflySpread(lowStrikePut, atmPutOption, atmCallOption, highStrikeCall)
        } else {
            return null
        }
    }

    private fun getOptionAt(
        dailyOptions: List<Option>,
        strike: Double,
        filt: (Option) -> Boolean
    ) = dailyOptions
        .filter { filt.invoke(it) }
        .minByOrNull { abs(it.strikePrice.toDouble() - strike) }

}

data class StrategyResponse(val profit: Double, val iv: Double, val executions: Int)

data class StrangleRequest(
    val symbol: String,
    val from: LocalDate,
    val to: LocalDate,
    val width: Double,
    val temporalDistance: Period,
    val frequency: Period
)
