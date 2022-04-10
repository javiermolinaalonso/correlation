package com.javislaptop.correlation.dataloader.eodhistoricaldata

import org.springframework.format.datetime.DateFormatter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.MessageFormat
import java.text.SimpleDateFormat

@Service
class DataLoaderEOD(private val restTemplate: RestTemplate) {

    companion object {
        val BASE_URL = "https://eodhistoricaldata.com/api/eod/{0}?api_token={1}&fmt={2}&period={3}&from={4}&to={5}"
    }

    fun loadData(dataLoaderRequest: DataLoaderRequestEOD) =
        restTemplate.getForEntity(format(dataLoaderRequest), DataLoaderResponse::class.java).body

    private fun format(r: DataLoaderRequestEOD) =
            MessageFormat.format(DataLoaderEOD.BASE_URL, r.symbol, r.token, r.format, r.period, r.from, r.to)
}