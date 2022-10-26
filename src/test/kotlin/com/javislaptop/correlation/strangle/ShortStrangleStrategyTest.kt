package com.javislaptop.correlation.strangle

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.coveredcall.CoveredCallFeatureRequest
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.FileReader
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
internal class ShortStrangleStrategyTest {

    @Autowired
    lateinit var victim : ShortStrangleStrategy

    @Test
    fun analyse() {
        CSVParser(
            FileReader("/Users/javi/IdeaProjects/correlation/src/test/resources/freeoptions.csv"),
            CSVFormat.DEFAULT
        ).forEach {
            //DDD.US	913.04%	7.8329
            //FCX.US	105.93%	6.1878
            //FCX.US	103.13%	5.9909
//            NFLX.US	-104.30%	5.4736
            val ticker = it[0] + ".US"
            val from = LocalDate.parse(it[1] + "01", DateTimeFormatter.ofPattern("yyyyMMdd"))
            val to = from.plusMonths(1)
            val analyse = victim.analyse(
                StrangleRequest(
                    ticker,
                    from,
                    to,
                    0.01,
                    Period.ofDays(14),
                    Period.ofDays(3)
                )
            )
//            if (!analyse.profit.isNaN()) {
//                println(String.format("%s, %.2f%%, %.2f, %s", ticker, analyse.profit * 100, analyse.iv, analyse.executions))
//            }
        }

    }
}