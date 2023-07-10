package com.javislaptop.correlation.model

import java.time.Instant
import java.time.LocalDate

data class StockEOD(val date: LocalDate, val open: Double, val high: Double, val low: Double, val close: Double, val adjustedClose: Double, val volume: Double)

data class StockInstant(val instant: Instant, val bid: Double, val ask: Double)
