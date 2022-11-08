package com.javislaptop.utils

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation

class VolatilityUtils {
    companion object {
        fun getDailyVolatility(endValues: DoubleArray): Double {
            val standardDeviation = StandardDeviation()
            val percentDifferences = DoubleArray(endValues.size - 1)
            for (i in 1 until endValues.size) {
                percentDifferences[i - 1] = (endValues[i] - endValues[i - 1]) / endValues[i - 1]
            }
            return standardDeviation.evaluate(percentDifferences)
        }
    }
}