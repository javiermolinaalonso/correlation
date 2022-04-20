package com.javislaptop.correlation.dataloader.deltaneutral

import java.time.LocalDate

data class DeltaNeutralOption(
    val underlyingSymbol: String,
    val underlyingPrice: Double,
    val optionSymbol: String,
    val type: String,
    val date: LocalDate,
    val expiration: LocalDate,
    val strike: Double,
    val last: Double,
    val bid: Double,
    val ask: Double,
    val volume: Double,
    val openInterest: Int
)