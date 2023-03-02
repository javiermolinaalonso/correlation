package com.javislaptop.correlation.dataloader.options

import com.assets.options.entities.Option

interface OptionsLoader {
    fun loadUnderlyingPrice(request: OptionsLoaderRequest): Double?

    fun load(request: OptionsLoaderRequest): List<Option>

}