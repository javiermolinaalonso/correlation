package com.javislaptop.regression

import com.javislaptop.correlation.model.StockEOD
import net.finmath.montecarlo.conditionalexpectation.LinearRegression
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoint
import org.apache.commons.math3.stat.regression.RegressionResults
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import kotlin.math.pow
import kotlin.math.roundToInt

@Service
class PolinomialRegression {

    fun compute(input : List<StockEOD>, degree: Int) : DoubleArray {
        val fitter = PolynomialCurveFitter.create(degree)
        val points = input.mapIndexed { i, v -> WeightedObservedPoint(1.0, i.toDouble(), v.close) }
        return fitter.fit(points)
    }

    fun computeWeighted(input : List<StockEOD>, degree: Int) : DoubleArray {
        val fitter = PolynomialCurveFitter.create(degree)
        val points = input.mapIndexed { i, v -> WeightedObservedPoint(getWeight(i, input.size), i.toDouble(), v.close) }
        return fitter.fit(points)
    }

    private fun getWeight(value: Int, size: Int): Double {
//        return value.toDouble() * (2.0.pow(ln(value.toDouble())))
        return 1.0
    }

    fun predict(input : List<StockEOD>, degree: Int, n : Int) = predict(computeWeighted(input, degree), n)

    fun predictLinear(values : List<StockEOD>, n : Int) : Double {
        val rs = SimpleRegression()
        values.forEachIndexed{i,v -> rs.addData(i.toDouble(), v.close)}
        return rs.predict(n.toDouble())
    }
    fun predict(values : DoubleArray, n : Int) : Double {
        if (values.size < 2) throw RuntimeException("Invalid value")

        var result = values[0]

        for (i in 1 until values.size) {
            result += values[i] * n.toDouble().pow(i.toDouble())
        }

        return result.times(100).roundToInt() / 100.0
    }
}