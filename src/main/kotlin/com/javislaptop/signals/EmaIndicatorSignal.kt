package com.javislaptop.signals

import com.javislaptop.correlation.model.StockEOD
import com.javislaptop.utils.BarUtils
import org.ta4j.core.indicators.AwesomeOscillatorIndicator
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.rules.IsRisingRule
import org.ta4j.core.rules.UnderIndicatorRule
import java.time.LocalDate

class EmaIndicatorSignal(val shortEma: Int, val longEma: Int) : Signal {

    override fun execute(data : List<StockEOD>) : List<LocalDate> {
        val series = BarUtils.convertBars(data)
        val cpi = ClosePriceIndicator(series)
        val shortEmaIndicator = EMAIndicator(cpi, shortEma)
        val longEmaIndicator = EMAIndicator(cpi, longEma)

        val underIndicatorRule  = UnderIndicatorRule(longEmaIndicator, shortEmaIndicator)
        var r = mutableListOf<LocalDate>()
        for (i in 3 until series.barCount) {
            // should be satisfied when longEma value is under shortEma
            if (underIndicatorRule.isSatisfied(i)) {
                r.add(series.getBar(i).endTime.toLocalDate())
            }
        }
        return r
    }

    override fun indicator() = "EMA"
}