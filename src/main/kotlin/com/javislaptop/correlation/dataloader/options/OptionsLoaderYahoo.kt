package com.javislaptop.correlation.dataloader.options

import com.assets.options.book.loader.yahoo.OptionBookLoaderYahooOffline
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@Primary
class OptionsLoaderYahoo : OptionsLoader {
    override fun loadUnderlyingPrice(request: OptionsLoaderRequest) =
        OptionBookLoaderYahooOffline.load(request.date, request.symbol).map { it.currentPrice }.orElse(BigDecimal.ZERO).toDouble()

    override fun load(request: OptionsLoaderRequest) =
        OptionBookLoaderYahooOffline.load(request.date, request.symbol).map { it.options }.orElse(listOf())
}