package com.javislaptop.correlation.dataloader.eodhistoricaldata

import java.time.Instant
import java.time.LocalDate

data class DataLoaderRequestEOD(
        val symbol: String,
        val token: String,
        val format: String,
        val from: LocalDate,
        val to: LocalDate,
        val period: String,
        val order: String
)
