package com.javislaptop.correlation.dataloader.eodhistoricaldata.options

import java.time.LocalDate

data class DataLoaderRequestOptionsEOD(
        val symbol: String,
        val token: String,
        val from: LocalDate,
        val to: LocalDate
)
