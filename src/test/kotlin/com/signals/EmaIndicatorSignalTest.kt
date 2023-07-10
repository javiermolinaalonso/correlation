package com.signals

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.signals.AwesomeOscillatorSignal
import com.javislaptop.signals.EmaIndicatorSignal
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class EmaIndicatorSignalTest {
    val from = LocalDate.of(2010, 1, 1)
    val to = LocalDate.of(2022, 3, 31)

    @Autowired
    lateinit var dataLoader: DataLoaderDynamoDb

    val victim = EmaIndicatorSignal(7, 30)

    @Test
    fun foo() {
        val data = dataLoader.loadData(DataLoaderRequestDynamoDb("SPY", from, to))

        val res = victim.execute(data)

        res.forEach { println(it) }
    }
}