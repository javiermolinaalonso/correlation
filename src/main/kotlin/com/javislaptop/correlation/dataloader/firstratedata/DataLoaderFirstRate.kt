package com.javislaptop.correlation.dataloader.firstratedata


import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.ta4j.core.Bar
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import java.io.FileReader
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DataLoaderFirstRate {

    fun loadData(r: RequestParams) : BarSeries {
        return BaseBarSeries(CSVParser(
                FileReader(r.f),
                CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build()
        )
                .map { parseItem(it,r) }
                .toList())
    }

    private fun parseItem(it: CSVRecord, r: RequestParams) : Bar {
        val dateTime = LocalDateTime.parse(it["timestamp"], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return BaseBar(r.duration, ZonedDateTime.of(dateTime, ZoneId.of("US/Eastern")).plus(r.duration), it["open"], it["high"], it["low"], it["close"], it["volume"])
    }

    data class RequestParams(val f: String, val duration : Duration)

}