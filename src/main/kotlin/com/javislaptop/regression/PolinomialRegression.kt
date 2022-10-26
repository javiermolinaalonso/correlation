package com.javislaptop.regression

import com.javislaptop.correlation.model.StockEOD
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoint
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import kotlin.math.pow

@Service
class PolinomialRegression {

    fun compute(input : List<StockEOD>, degree: Int) : DoubleArray {
        val fitter = PolynomialCurveFitter.create(degree)
        val points = input.mapIndexed { i, v -> WeightedObservedPoint(1.0, i.toDouble(), v.close) }
        return fitter.fit(points)
    }

    fun predict(values : DoubleArray, n : Int) : Double {
        if (values.size < 2) throw RuntimeException("Invalid value")

        var result = values[0]

        for (i in 1 until values.size) {
            result += values[i] * n.toDouble().pow(i.toDouble())
        }

        return result
    }
}