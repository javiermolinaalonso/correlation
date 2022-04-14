package com.javislaptop.correlation.dataloader.eodhistoricaldata.stock

import com.javislaptop.correlation.dataloader.eodhistoricaldata.EodProperties
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.MessageFormat

@Service
class DataLoaderEOD(private val restTemplate: RestTemplate, val properties: EodProperties) {

    companion object {
        val BASE_URL = "https://eodhistoricaldata.com/api/eod/{0}?api_token={1}&fmt={2}&period={3}&from={4}&to={5}"
    }

    fun loadData(dataLoaderRequest: DataLoaderRequestEOD) =
        restTemplate.getForEntity(format(dataLoaderRequest), DataLoaderResponse::class.java).body

    private fun format(r: DataLoaderRequestEOD) =
        MessageFormat.format(BASE_URL, r.symbol, properties.token, r.format, r.period, r.from, r.to)
}