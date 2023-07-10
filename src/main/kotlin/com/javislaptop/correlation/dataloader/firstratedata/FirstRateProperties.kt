package com.javislaptop.correlation.dataloader.firstratedata

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
//@ConfigurationProperties(prefix = "correlation.firstrate")
class FirstRateProperties(
        val basepath: String,
        val fileprefix: String){

}