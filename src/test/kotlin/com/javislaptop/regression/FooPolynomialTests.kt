package com.javislaptop.regression

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.utils.VolatilityUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.math.absoluteValue

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class FooPolynomialTests {

    val from = LocalDate.of(2021, 10, 6)
    val to = LocalDate.of(2022, 3, 15)

    @Autowired
    lateinit var victim: DataLoaderDynamoDb

    val polinomialRegression = PolinomialRegression()

    @Test
    internal fun foo() {
//        val tickers = listOf("AAPL", "AMZN", "BLK", "DIS", "GIS", "GOOG", "INTC", "JNJ", "NVDA", "PEP", "KO", "PM", "PG", "SBUX", "V", "MA", "WM")
        val tickers = listOf("SPY")

        println("Current day, Predicted day, last value, predicted value, real value, accuracy, stdDev, lowerRange, upperRange, inRange")
        for (ticker in tickers) {
            val dataRequest = DataLoaderRequestDynamoDb(ticker, from, to)
            val data = victim.loadData(dataRequest)

//            val daysToPredict = 10
            val c = 2
            val inputdays = 100
            for (daysToPredict in 7 .. 7) {
                val accuracy = mutableListOf<Double>()
                val stddevs = mutableListOf<Double>()
                var inRangeSuccess = 0
                var count = 0
                for (i in inputdays until data.size - daysToPredict step 10) {
                    val longList = data.subList(i - inputdays, i)
                    val closingPrices = longList.map { it.close }.toDoubleArray()
                    val target = data[i + daysToPredict].close
                    val coeff = polinomialRegression.computeWeighted(longList, c)
                    println("${coeff[0]}, ${coeff[1]}, ${coeff[2]}")
                    longList.forEach{println("${it.close}")}
                    val prediction = polinomialRegression.predictLinear(longList, daysToPredict + inputdays)
                    val distance = ((target - prediction) / target).absoluteValue
                    val lastValue = data[i].close

                    val stdDev =
                        VolatilityUtils.getDailyVolatility(closingPrices).times(Math.sqrt(daysToPredict.toDouble()))
                    val upperRange = prediction + roundTwoDecimals(prediction * stdDev)
                    val lowerRange = prediction - roundTwoDecimals(prediction * stdDev)
                    val inRange = lowerRange < target && target < upperRange
//                    println(
//                        "${data[i].date}, ${data[i + daysToPredict].date}, $lastValue, $prediction, $target, ${
//                            roundTwoDecimals(
//                                distance
//                            )
//                        }, ${roundTwoDecimals(stdDev)}, ${roundTwoDecimals(lowerRange)}, ${roundTwoDecimals(upperRange)}, $inRange"
//                    )
                    if (inRange) inRangeSuccess++
                    accuracy.add(distance)
                    stddevs.add(stdDev)
                    count++
                }

//                println("DaysPredict: $daysToPredict. Ticker: $ticker. Coefficient: $c. InputDays: $inputdays. Accuracy: ${accuracy.average()}. StdDev: ${stddevs.average()}. InRange: ${inRangeSuccess.toDouble() / count.toDouble()}")
            }
        }
    }

    private fun roundTwoDecimals(distance: Double) = distance.times(10000).toInt() / 10000.0
}