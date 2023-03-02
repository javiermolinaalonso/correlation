package com.javislaptop.distribution

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.math.absoluteValue

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class DistributionTest {

    val from = LocalDate.of(2020, 1, 1)
    val to = LocalDate.of(2022, 3, 15)

    @Autowired
    lateinit var victim: DataLoaderDynamoDb

    @Test
    internal fun distribute() {
        val ticker = "SPY"
        val data = victim.loadData(DataLoaderRequestDynamoDb(ticker, from, to))

        val step = 22
        for (i in 0 until data.size - step) {
            val j = i + step
            val m = (data[j].close - data[i].close) / data[i].close
            println("${data[j].close}, ${m.absoluteValue}")
        }
    }
}