package com.javislaptop.signals

import com.javislaptop.correlation.model.StockEOD
import com.javislaptop.utils.BarUtils
import org.ta4j.core.indicators.AroonDownIndicator
import org.ta4j.core.indicators.AroonUpIndicator
import org.ta4j.core.rules.IsRisingRule
import org.ta4j.core.rules.UnderIndicatorRule
import java.time.LocalDate

class AroonIndicatorSignal (private val barCount: Int, private val threshold: Int) : Signal {

    override fun execute(data : List<StockEOD>) : List<LocalDate> {
        val series = BarUtils.convertBars(data)
        val ad = AroonDownIndicator(series, barCount)
        val au = AroonUpIndicator(series, barCount)

        val auRule = UnderIndicatorRule(au, threshold)
        val adRule = UnderIndicatorRule(ad, threshold)
        var r = mutableListOf<LocalDate>()
        for (i in barCount until series.barCount) {
            if (adRule.isSatisfied(i) && auRule.isSatisfied(i).not()) { //Aaron should be over 50
                r.add(series.getBar(i).endTime.toLocalDate())
            }
        }
        return r
    }

    override fun indicator() = "Aroon"

}