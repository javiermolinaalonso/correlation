package com.javislaptop.utils

import com.javislaptop.correlation.model.StockEOD
import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.num.Num
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.function.Function
import java.util.stream.Collectors

class BarUtils {

    companion object {
        fun convertBars(candlesticks: List<StockEOD>): BaseBarSeries {
            val bars: MutableList<Bar> = candlesticks.stream()
                .map { BaseBar(
                    Duration.ofDays(1),
                    ZonedDateTime.of(it.date, LocalTime.MAX, ZoneId.systemDefault()),
                    it.open,
                    it.high,
                    it.low,
                    it.close,
                    it.volume
                ) }
                .toList()
                .toMutableList()
            return BaseBarSeriesBuilder()
                .withBars(bars)
                .build();
        }
    }
}