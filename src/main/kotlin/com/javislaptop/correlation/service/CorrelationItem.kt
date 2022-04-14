package com.javislaptop.correlation.service

import java.time.LocalDate

data class CorrelationItem(val date : LocalDate, val value : Double, val amountOfItems: Int, val symbol : String, val symbol2 : String) {

    override fun toString() = String.format("%s, %s, %s, %s, %.4f", symbol, symbol2, date, amountOfItems, value)
}
