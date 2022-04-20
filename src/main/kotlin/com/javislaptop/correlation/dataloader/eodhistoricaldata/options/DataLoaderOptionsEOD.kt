package com.javislaptop.correlation.dataloader.eodhistoricaldata.options

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.MessageFormat

@Service
class DataLoaderOptionsEOD(private val restTemplate: RestTemplate) {
    companion object {
        val BASE_URL = "https://eodhistoricaldata.com/api/options/{0}?api_token={1}&trade_date_from={2}&trade_date_to={3}"
    }

    fun loadData(dataLoaderRequest: DataLoaderRequestOptionsEOD) =
            restTemplate.getForEntity(format(dataLoaderRequest), OptionResponseEOD::class.java).body

    private fun format(r: DataLoaderRequestOptionsEOD) =
            MessageFormat.format(BASE_URL, r.symbol, r.token, r.from, r.to)
}