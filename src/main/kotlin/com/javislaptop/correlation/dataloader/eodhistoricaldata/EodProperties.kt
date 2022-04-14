package com.javislaptop.correlation.dataloader.eodhistoricaldata

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "correlation.eod")
data class EodProperties(val token : String)
