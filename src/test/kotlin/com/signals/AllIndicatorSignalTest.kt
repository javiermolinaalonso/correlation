package com.signals

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.signals.AroonIndicatorSignal
import com.javislaptop.signals.AwesomeOscillatorSignal
import com.javislaptop.signals.EmaIndicatorSignal
import com.javislaptop.signals.Signal
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class AllIndicatorSignalTest {

    val from = LocalDate.of(2021, 1, 1)
    val to = LocalDate.of(2022, 3, 31)

    @Autowired
    lateinit var dataLoader: DataLoaderDynamoDb

    val victim = listOf(AroonIndicatorSignal(25, 50), AwesomeOscillatorSignal(), EmaIndicatorSignal(7, 30))

    @Test
    fun foo() {
        val data = dataLoader.loadData(DataLoaderRequestDynamoDb("SPY", from, to))

//        val values = victim.map { Pair(it.indicator(), it.execute(data)) }.groupBy { it.first }
        val dates = victim.map { it.execute(data).toHashSet() }.reduce { acc, date -> acc.apply { retainAll(date) } }
        dates.sorted().forEach { println(it) }
    }

}