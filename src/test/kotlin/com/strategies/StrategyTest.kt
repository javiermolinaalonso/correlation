package com.strategies

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.indicators.AwesomeOscillatorIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.helpers.HighestValueIndicator
import org.ta4j.core.indicators.helpers.LowestValueIndicator
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.Num
import org.ta4j.core.rules.IsFallingRule
import org.ta4j.core.rules.IsRisingRule
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class StrategyTest {
    val SELL_BARS_FALLING = 3
    val SELL_BARS_RISING = 5

    private val BUY_BARS_RISING = 3
    private val BUY_BARS_FALLING = 5

    val from = LocalDate.of(2020, 1, 1)
    val to = LocalDate.of(2022, 2, 1)

    @Autowired
    lateinit var victim: DataLoaderDynamoDb

    @Test
    internal fun awesomeTest() {

        val logger: Logger = LogManager.getLogger(
            StrategyTest::class.java
        )
        // Add a minimum decrease of 10% in three bars
        // There should not be a
        // only Buy if the AO is negative
        // only sell if AO is (very)positive
        val data = victim.loadData(DataLoaderRequestDynamoDb("DIS", from, to))

        val series = BaseBarSeriesBuilder().withBars(data.map {
            BaseBar(
                Duration.ofDays(1),
                it.date.plusDays(1).atStartOfDay(ZoneId.systemDefault()),
                it.open,
                it.high,
                it.low,
                it.close,
                it.volume
            )
        }).build()

        val closePriceIndicator = ClosePriceIndicator(series)
        val ao = AwesomeOscillatorIndicator(closePriceIndicator)
        val hvi = HighestValueIndicator(closePriceIndicator, 10)
        val lvi = LowestValueIndicator(closePriceIndicator, 10)
        val rising = IsRisingRule(ao, SELL_BARS_RISING)
        val falling = IsFallingRule(ao, SELL_BARS_FALLING)
        val buyRising = IsRisingRule(ao, BUY_BARS_RISING)
        val buyFalling = IsFallingRule(ao, BUY_BARS_FALLING)

        val startingIndex = 60

        var buyPrice: Num? = null
        var sellPrice: Num? = null

        var differenceBuySell = 0
        for (i in startingIndex until series.barCount) {
            if (sellPrice != null) {
                val currentBar: Bar = series.getBar(i)
                if (currentBar.highPrice.isGreaterThanOrEqual(sellPrice)) {
                    logger.info(
                        "{}, SELL, {}",
                        currentBar.beginTime,
                        sellPrice
                    )
                    sellPrice = null
                    differenceBuySell++
                }
            }
            if (buyPrice != null) {
                val currentBar: Bar = series.getBar(i)
                if (currentBar.lowPrice.isLessThanOrEqual(buyPrice)) {
                    logger.info(
                        "{}, BUY, {}",
                        currentBar.beginTime,
                        buyPrice
                    )
                    buyPrice = null
                    differenceBuySell--
                }
            }
            val highestAOValue = ao.getValue(i - SELL_BARS_FALLING)
            if (rising.isSatisfied(i - SELL_BARS_FALLING) && falling.isSatisfied(i)) {
                val thresholdAO: Num = analyseAo(ao, i, true)
                if (highestAOValue.isGreaterThan(thresholdAO)) {
                    sellPrice = hvi.getValue(i)
                }
            }
            if (buyFalling.isSatisfied(i - BUY_BARS_RISING) && buyRising.isSatisfied(i)) {
                val thresholdAO: Num = analyseAo(ao, i, false)
                if (highestAOValue.isLessThan(thresholdAO)) {
                    buyPrice = lvi.getValue(i)
                }
            }
        }
        logger.info(differenceBuySell)
    }

    private fun analyseAo(ao: AwesomeOscillatorIndicator, index: Int, positive: Boolean): Num {
        val aos: MutableList<Double> = ArrayList()
        for (i in index - 60 until index) {
            if (positive) {
                if (ao.getValue(i).isPositive) aos.add(ao.getValue(i).doubleValue())
            } else {
                if (ao.getValue(i).isNegative) {
                    aos.add(ao.getValue(i).doubleValue())
                }
            }
        }
        return DoubleNum.valueOf(aos.stream().mapToDouble { t: Double? -> t!! }.average().orElse(0.0))
    }
}