package com.javislaptop.regression

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class FooPolynomialTests {

    val from = LocalDate.of(2010, 11, 1)
    val to = LocalDate.of(2022, 2, 1)

    @Autowired
    lateinit var victim: DataLoaderDynamoDb

    val polinomialRegression = PolinomialRegression()

    @Test
    internal fun foo() {
        val dataRequest = DataLoaderRequestDynamoDb("PEP.US", from, to)
        val data = victim.loadData(dataRequest)

        val daysToPredict = 30
        val inputdays = 60
        val c = 2
        val accuracyByCoefficients = mutableMapOf<Int, MutableList<Double>>()
        for (i in inputdays until data.size - daysToPredict) {
            val trainingList = data.subList(i - inputdays, i)
            val target = data[i + daysToPredict].close
            val coefficients = polinomialRegression.compute(trainingList, c)
            val predict = polinomialRegression.predict(coefficients, daysToPredict + inputdays)
            val distance = ((target - predict) / target).absoluteValue * 100.0
            println("Coefficient: $c. Evaluating at ${data[i].date}. Expected value at ${data[i + daysToPredict].date}: $predict. Real value: ${target}. Accuracy: ${distance.roundToInt()}")
            accuracyByCoefficients.putIfAbsent(c, mutableListOf())
            accuracyByCoefficients[c]!!.add(distance)
        }

        accuracyByCoefficients.forEach { println("${it.key}: ${it.value.average()}") }
    }
}