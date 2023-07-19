package com

import com.assets.options.book.OptionBook
import com.assets.options.entities.*
import com.assets.options.entities.spread.SpreadFactory
import com.assets.options.entities.spread.neutral.IronCondorSpread
import com.javislaptop.correlation.dataloader.options.OptionsLoaderRequest
import com.javislaptop.correlation.dataloader.options.OptionsLoaderYahoo
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

private const val CONFIDENCE_95 = 1.96

class OptionsLoaderTest {

    val from = LocalDate.of(2020, 7, 10)

    val to = LocalDate.of(2023, 10, 31)
    val optionsLoader = OptionsLoaderYahoo()

    var profit = 0.0
    var tradeProfit = 0.0

    @Test
    fun testVolatility() {
        val ticker = "SPY"

        for (date in from.datesUntil(to.minusMonths(2), Period.ofWeeks(1))) {
            val book = loadBook(ticker, date)
            val targetDate = getTargetDate(date, book)
            val impliedVolatility = (book.getVolatility(targetDate) * 100).roundToInt() / 100.0
            val currentPrice = book.currentPrice.toDouble()
            val days = ChronoUnit.DAYS.between(date, targetDate).toDouble()

            val rangeOneStandardDeviation = currentPrice * impliedVolatility * Math.sqrt(days / 365)
            val range95Confidence = currentPrice * impliedVolatility * Math.sqrt(days / 365) * CONFIDENCE_95

            val longPutPrice = currentPrice - range95Confidence
            val shortPutPrice = currentPrice - rangeOneStandardDeviation
            val shortCallPrice = currentPrice + rangeOneStandardDeviation
            val longCallPrice = currentPrice + range95Confidence

            val longPut = book.getClosestOption(targetDate, BigDecimal(longPutPrice), false).map {PutOption(it)}.get()
            val shortPut = book.getClosestOption(targetDate, BigDecimal(shortPutPrice), false).map {PutOption(it)}.get()
            val shortCall = book.getClosestOption(targetDate, BigDecimal(shortCallPrice), true).map {CallOption(it)}.get()
            val longCall = book.getClosestOption(targetDate, BigDecimal(longCallPrice), true).map { CallOption(it) }.get()

            val ic = SpreadFactory().ironCondor(longPut, shortPut, shortCall, longCall, 1)
            val avgProfit = ic.maxGain.toDouble() * 0.65 + ic.maxLoss.toDouble() * 0.05
//            println("Avg profit: $avgProfit")

            val targetBook = evaluateResult(ticker, targetDate, ic)
            val finalPrice = targetBook.currentPrice

            val isMaxProfit = finalPrice > ic.s2 && finalPrice < ic.s3
            val isAvg = finalPrice > ic.s1 && finalPrice < ic.s4 && !isMaxProfit
            val isMaxLoss = !isAvg && !isMaxProfit

            var r = ""
            if (isMaxLoss) {
                r = "MAX_LOSS"
            } else if (isAvg) {
                r = "AVG"
            } else {
                r = "MAX_PROFIT"
            }
            println("$date, $targetDate, $tradeProfit, $impliedVolatility, [${ic.s1},${ic.s2},${ic.s3},${ic.s4}], ${targetBook.currentPrice}, $r")
//            println("$profit")
        }

    }

    private fun evaluateResult(ticker: String, targetDate: LocalDate, ironCondor: IronCondorSpread) : OptionBook {
        val book = loadBook(ticker, targetDate)

        val targetCondor = SpreadFactory().ironCondor(
            book.getPutOption(targetDate, ironCondor.s1),
            book.getPutOption(targetDate, ironCondor.s2),
            book.getCallOption(targetDate, ironCondor.s3),
            book.getCallOption(targetDate, ironCondor.s4),
            1
        )
//        println("Purchased condor: $ironCondor")
//        println("Sold condor: $targetCondor")
//        println("Premium received: ${ironCondor.cost}. Premium at expiration: ${targetCondor.cost}. Profit: ${targetCondor.cost-ironCondor.cost}")
//        println("${targetCondor.expirationDate}, ${targetCondor.cost-ironCondor.cost}")
        tradeProfit = targetCondor.cost.toDouble() - ironCondor.cost.toDouble()
        profit += targetCondor.cost.toDouble() - ironCondor.cost.toDouble()
        return book
    }
    private fun getTargetDate(date: LocalDate, book: OptionBook): LocalDate {
        val targetDate = date.plusMonths(1)
            .withDayOfMonth(1)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
            .plusWeeks(2)
        if(!book.availableDates.contains(targetDate) || ChronoUnit.DAYS.between(date, targetDate) < 30) {
            return date.plusMonths(2)
                .withDayOfMonth(1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                .plusWeeks(2)
        }
        return targetDate;
    }

    private fun loadBook(ticker: String, date: LocalDate): OptionBook {
        val options = optionsLoader.load(OptionsLoaderRequest(ticker, date))
        return OptionBook.Builder.create()
            .withTicker(ticker)
            .withNow(date)
            .withCurrentPrice(options.get(0).currentPrice)
            .withOptions(options)
            .build()
    }

    @Test
    fun name() {
        val ticker = "SPY"
        val from = LocalDate.of(2020, 4, 1)
        val to = LocalDate.of(2023, 4, 1)

        val data = HashMap<LocalDate, OptionBook>()
        for (date in from.datesUntil(to)) {
            val options = optionsLoader.load(OptionsLoaderRequest(ticker, date))
            if (options.isNotEmpty()) {
                val book = OptionBook.Builder.create()
                    .withTicker(ticker)
                    .withNow(date)
                    .withCurrentPrice(options.get(0).currentPrice)
                    .withOptions(options)
                    .build()
                data[date] = book
                println(date)
            }
        }

        println(data)
    }
}