package com.javislaptop.correlation.dataloader.deltaneutral

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "correlation.deltaneutral")
data class DeltaNeutralProperties(
    val basepath: String,
    val fileprefix: String,
    val pattern: String)