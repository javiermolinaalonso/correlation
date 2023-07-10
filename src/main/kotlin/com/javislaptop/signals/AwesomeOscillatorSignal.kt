package com.javislaptop.signals

import com.javislaptop.correlation.model.StockEOD
import com.javislaptop.utils.BarUtils
import org.ta4j.core.indicators.AwesomeOscillatorIndicator
import org.ta4j.core.rules.IsRisingRule
import java.time.LocalDate

class AwesomeOscillatorSignal : Signal {

    override fun execute(data : List<StockEOD>) : List<LocalDate> {
        val series = BarUtils.convertBars(data)
        val ao = AwesomeOscillatorIndicator(series)

        val isRisingRule = IsRisingRule(ao, 3)

        var r = mutableListOf<LocalDate>()
        for (i in 3 until series.barCount) {
            if (isRisingRule.isSatisfied(i)) {
                r.add(series.getBar(i).endTime.toLocalDate())
            }
        }
        return r
    }

    override fun indicator() = "Awesome"
}