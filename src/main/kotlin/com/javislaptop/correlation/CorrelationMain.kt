package com.javislaptop.correlation

import com.javislaptop.correlation.dataloader.eodhistoricaldata.EodProperties
import com.javislaptop.correlation.datastore.DynamoDbProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(value = arrayOf(DynamoDbProperties::class, EodProperties::class))
class CorrelationMain {

    fun main(args: Array<String>) {
        runApplication<CorrelationMain>(*args)
    }
}
