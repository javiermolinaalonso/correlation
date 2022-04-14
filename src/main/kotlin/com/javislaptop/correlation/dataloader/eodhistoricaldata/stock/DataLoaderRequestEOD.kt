package com.javislaptop.correlation.dataloader.eodhistoricaldata.stock

import java.time.LocalDate

data class DataLoaderRequestEOD(
        val symbol: String,
        val format: String,
        val from: LocalDate,
        val to: LocalDate,
        val period: String,
        val order: String
)
