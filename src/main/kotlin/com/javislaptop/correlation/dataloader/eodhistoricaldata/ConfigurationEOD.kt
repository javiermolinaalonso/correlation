package com.javislaptop.correlation.dataloader.eodhistoricaldata

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.datetime.DateFormatter
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat

@Configuration
class ConfigurationEOD {

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun objMapper() = jacksonObjectMapper()
}