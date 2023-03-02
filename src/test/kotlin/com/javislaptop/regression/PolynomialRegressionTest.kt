package com.javislaptop.regression

import com.javislaptop.correlation.model.StockEOD
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.math.pow

class PolynomialRegressionTest {

    val victim = PolinomialRegression()

    @Test
    internal fun testPolynomialRegression() {
        val data = listOf(
            StockEOD(LocalDate.of(2000, 1, 3), 35.375, 37.375, 35.0, 36.875, 0.0, 5297700.0),
            StockEOD(LocalDate.of(2000, 1, 4), 36.5625, 37.5625, 35.75, 35.9375, 0.0, 4555700.0),
            StockEOD(LocalDate.of(2000, 1, 5), 35.8125, 36.3125, 34.875, 35.0625, 0.0, 5701900.0),
            StockEOD(LocalDate.of(2000, 1, 6), 34.75, 36.875, 34.5, 36.625, 0.0, 6863100.0),
            StockEOD(LocalDate.of(2000, 1, 7), 36.875, 38.6253, 36.6875, 38.25, 0.0, 0523600.0),
            StockEOD(LocalDate.of(2000, 1, 10), 38.125, 38.1875, 36.625, 36.875, 0.0, 6402500.0),
            StockEOD(LocalDate.of(2000, 1, 11), 37.25, 37.625, 36.625, 36.75, 0.0, 5611600.0),
            StockEOD(LocalDate.of(2000, 1, 12), 36.75, 37.25, 36.3125, 36.3125, 0.0, 4267300.0),
            StockEOD(LocalDate.of(2000, 1, 13), 36.3125, 36.625, 35.9375, 35.9375, 0.0, 3144200.0),
            StockEOD(LocalDate.of(2000, 1, 14), 36.0625, 36.8125, 35.9375, 36.1875, 0.0, 5413700.0),
            StockEOD(LocalDate.of(2000, 1, 18), 35.9375, 37.0, 35.5625, 36.25, 0.0, 4821600.0),
            StockEOD(LocalDate.of(2000, 1, 19), 36.875, 36.875, 35.9375, 36.4375, 0.0, 3999700.0),
            StockEOD(LocalDate.of(2000, 1, 20), 36.4375, 36.4375, 36.0, 36.125, 0.0, 3106100.0),
            StockEOD(LocalDate.of(2000, 1, 21), 36.1875, 36.4375, 35.75, 36.125, 0.0, 4454400.0),
            StockEOD(LocalDate.of(2000, 1, 24), 36.1875, 36.375, 34.8125, 35.3125, 0.0, 3785000.0),
            StockEOD(LocalDate.of(2000, 1, 25), 35.3125, 35.375, 33.875, 34.3125, 0.0, 4356500.0),
            StockEOD(LocalDate.of(2000, 1, 26), 34.3125, 34.9375, 33.75, 33.9375, 0.0, 3174100.0),
            StockEOD(LocalDate.of(2000, 1, 27), 34.0, 34.3125, 33.25, 33.8125, 0.0, 2610100.0),
            StockEOD(LocalDate.of(2000, 1, 28), 33.5625, 34.0, 33.1875, 33.25, 0.0, 4090800.0),
            StockEOD(LocalDate.of(2000, 1, 31), 33.5, 34.375, 33.25, 34.125, 0.0, 2989600.0),
            StockEOD(LocalDate.of(2000, 2, 1), 34.0, 34.3125, 33.6875, 34.0, 0.0, 3602600.0),
            StockEOD(LocalDate.of(2000, 2, 2), 33.75, 33.9375, 32.9375, 33.0, 0.0, 3289100.0),
            StockEOD(LocalDate.of(2000, 2, 3), 33.0, 33.375, 32.625, 33.0625, 0.0, 3662900.0),
            StockEOD(LocalDate.of(2000, 2, 4), 33.625, 34.125, 32.625, 32.75, 0.0, 3885600.0),
            StockEOD(LocalDate.of(2000, 2, 7), 32.875, 33.1875, 32.25, 32.375, 0.0, 3999700.0),
            StockEOD(LocalDate.of(2000, 2, 8), 33.3125, 33.75, 33.0, 33.3125, 0.0, 5010500.0),
            StockEOD(LocalDate.of(2000, 2, 9), 35.0, 35.125, 33.75, 34.0, 0.0, 8432400.0),
            StockEOD(LocalDate.of(2000, 2, 10), 34.875, 34.875, 34.0625, 34.625, 0.0, 5909500.0),
            StockEOD(LocalDate.of(2000, 2, 11), 35.0, 35.125, 34.0, 34.375, 0.0, 5530400.0),
            StockEOD(LocalDate.of(2000, 2, 14), 34.1875, 34.5, 33.9375, 34.0625, 0.0, 3493300.0),
            StockEOD(LocalDate.of(2000, 2, 15), 34.0, 34.875, 34.0, 34.1875, 0.0, 3909600.0),
            StockEOD(LocalDate.of(2000, 2, 16), 34.125, 34.8125, 33.9375, 34.6875, 0.0, 4606800.0),
            StockEOD(LocalDate.of(2000, 2, 17), 34.5625, 34.8125, 34.25, 34.3125, 0.0, 2847700.0),
            StockEOD(LocalDate.of(2000, 2, 18), 34.375, 34.75, 33.9375, 34.25, 0.0, 3650100.0),
            StockEOD(LocalDate.of(2000, 2, 22), 34.3125, 34.875, 34.125, 34.6875, 0.0, 2929400.0),
            StockEOD(LocalDate.of(2000, 2, 23), 34.3125, 34.75, 34.0, 34.1875, 0.0, 3249300.0),
            StockEOD(LocalDate.of(2000, 2, 24), 34.0625, 34.375, 33.1875, 34.0, 0.0, 3612800.0),
            StockEOD(LocalDate.of(2000, 2, 25), 34.0, 34.375, 32.6875, 32.75, 0.0, 4204700.0),
            StockEOD(LocalDate.of(2000, 2, 28), 33.1875, 33.875, 32.75, 33.25, 0.0, 3654700.0),
            StockEOD(LocalDate.of(2000, 2, 29), 32.875, 33.0625, 32.125, 32.125, 0.0, 4635500.0),
            StockEOD(LocalDate.of(2000, 3, 1), 32.0, 32.9375, 31.875, 32.5625, 0.0, 5496400.0),
            StockEOD(LocalDate.of(2000, 3, 2), 32.6875, 32.875, 31.5625, 31.5625, 0.0, 5170400.0),
            StockEOD(LocalDate.of(2000, 3, 3), 32.5, 32.5, 31.3125, 31.625, 0.0, 4626600.0),
            StockEOD(LocalDate.of(2000, 3, 6), 31.0625, 31.4375, 30.6875, 31.0, 0.0, 4457900.0),
            StockEOD(LocalDate.of(2000, 3, 7), 31.0, 31.5, 29.6875, 30.625, 0.0, 7053200.0),
            StockEOD(LocalDate.of(2000, 3, 8), 31.0, 31.5, 30.0625, 30.5, 0.0, 5159800.0),
            StockEOD(LocalDate.of(2000, 3, 9), 30.75, 31.75, 30.625, 31.375, 0.0, 3913200.0),
            StockEOD(LocalDate.of(2000, 3, 10), 31.0625, 31.375, 30.5, 30.75, 0.0, 3649100.0),
            StockEOD(LocalDate.of(2000, 3, 13), 30.125, 31.1875, 30.125, 31.125, 0.0, 4308000.0),
            StockEOD(LocalDate.of(2000, 3, 14), 30.625, 30.75, 30.125, 30.6875, 0.0, 4602700.0)
        )

        val r = victim.compute(data, 3)

        Assertions.assertEquals(r[0], 37.3935, 0.001)
        Assertions.assertEquals(r[1], -0.2737, 0.001)
        Assertions.assertEquals(r[2], 0.0087, 0.0001)
        Assertions.assertEquals(r[3], -0.000125, 0.00001)
    }

    @Test
    internal fun testPredict() {
        val coefs = doubleArrayOf(37.3935, -0.2737, 0.0087, -0.000125)
        val prediction = victim.predict(coefs, 55)
        val n = 55
        val fn = coefs[0] + coefs[1] * n + coefs[2] * n.toDouble().pow(2.0) + coefs[3] * n.toDouble().pow(3.0)


        MatcherAssert.assertThat(fn, Matchers.closeTo(prediction, 0.01));
        println("result at $n is $fn")
    }

}