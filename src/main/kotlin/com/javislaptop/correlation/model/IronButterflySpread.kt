package com.javislaptop.correlation.model

import com.assets.options.entities.Option
import java.time.LocalDate
import kotlin.math.max

class IronButterflySpread(
    val oomPut: Option,
    val atmPut: Option,
    val atmCall: Option,
    val oomCall: Option
) {

    init {
        require(oomCall.expirationDate.isEqual(oomPut.expirationDate) && oomCall.expirationDate.isEqual(atmCall.expirationDate) && oomCall.expirationDate.isEqual(atmPut.expirationDate))
    }

    fun premium() = atmCall.avgBidAsk + atmPut.avgBidAsk - oomPut.avgBidAsk - oomCall.avgBidAsk

    fun risk() = maxOf(
        oomCall.strikePrice.subtract(atmCall.strikePrice),
        atmPut.strikePrice.subtract(oomPut.strikePrice)
    ).subtract(premium()).toDouble()

    fun iv() = atmCall.impliedVolatility + atmPut.impliedVolatility - oomCall.impliedVolatility - oomPut.impliedVolatility

    fun expiryDate() = atmCall.expirationDate
}