package com.javislaptop.correlation.dataloader.eodhistoricaldata

import java.time.LocalDate

data class StockEOD(val date: LocalDate, val open: Double, val high: Double, val low: Double, val close: Double, val adjustedClose: Double, val volume: Double)
