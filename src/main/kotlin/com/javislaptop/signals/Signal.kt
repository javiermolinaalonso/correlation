package com.javislaptop.signals

import com.javislaptop.correlation.model.StockEOD
import java.time.LocalDate

interface Signal {
    fun execute(data : List<StockEOD>) : List<LocalDate>

    fun indicator() : String
}