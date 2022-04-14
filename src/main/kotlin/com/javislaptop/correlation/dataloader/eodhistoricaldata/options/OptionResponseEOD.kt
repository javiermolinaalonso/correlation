package com.javislaptop.correlation.dataloader.eodhistoricaldata.options

data class OptionResponseEOD(
    val code: String,
    val exchange: String,
    val data: List<OptionDataEOD>
)

data class OptionDataEOD(
    val expirationDate: String,
    val options: Options
)

data class Options (
    val CALL : List<OptionEOD>,
    val PUT : List<OptionEOD>
)

data class OptionEOD(
    val contractName: String,
    val contractSize: String,
    val currency: String,
    val type: String,
    val expirationDate: String,
    val strike: String,
    val lastPrice: String,
    val bid: String,
    val ask: String,
//    val change: String,
//    val changePercent: String,
    val volume: Int,
    val openInterest: Int,
    val impliedVolatility: String,
    val delta: String,
    val gamma: String,
    val theta: String,
    val vega: String,
    val rho: String,
//    val theoretical: String,
//    val intrinsicValue: String,
//    val timeValue: String
)
