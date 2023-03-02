package com.javislaptop.correlation.strangle

import com.javislaptop.correlation.CorrelationMain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.Period
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class ShortStrangleSpyTest {

    @Autowired
    lateinit var victim: ShortStrangleStrategy

    @Test
    fun analyse() {
        val from = LocalDate.of(2020, 4, 1)
        val to = LocalDate.of(2021, 4, 1)
        val ticker = "SPY"
        val pool = Executors.newFixedThreadPool(8)
        println("frequency, width, period, profit")
        for (frequency in (7..60) step 7) {
            for (period in (7..60) step 7) {
                for (width in (1..20)) {
                    pool.submit {
                        val r = victim.analyse(
                            StrangleRequest(
                                ticker,
                                from,
                                to,
                                width / 100.0,
                                Period.ofDays(period),
                                Period.ofDays(frequency)
                            )
                        )
                        println("$frequency, $width, $period, ${r.profit}")
                    }
                }
            }
        }
        pool.awaitTermination(1, TimeUnit.HOURS)
    }
}