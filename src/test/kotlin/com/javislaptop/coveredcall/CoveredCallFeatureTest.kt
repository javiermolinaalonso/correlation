package com.javislaptop.coveredcall

import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.coveredcall.CoveredCallFeature
import com.javislaptop.correlation.coveredcall.CoveredCallFeatureRequest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.Period

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
internal class CoveredCallFeatureTest {

    @Autowired
    lateinit var victim : CoveredCallFeature

    @Test
    fun analyse() {

        val analyse = victim.analyse(
            CoveredCallFeatureRequest(
                "AMZN.US",
                LocalDate.of(2018, 11, 1),
                LocalDate.of(2018, 11, 30),
                0.01,
                Period.ofDays(3),
                Period.ofDays(3)
            )
        )
        println("Strategy profit: $analyse")
    }
}