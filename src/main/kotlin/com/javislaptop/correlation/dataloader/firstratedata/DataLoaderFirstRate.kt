package com.javislaptop.correlation.dataloader.firstratedata


import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarBuilder
import java.io.FileReader

class DataLoaderFirstRate {

    fun loadData(r: RequestParams) : List<Bar> {
        CSVParser(
                FileReader(r.f),
                CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build()
        )
                .map { parseItem(it) }
                .toList()
    }

    private fun parseItem(it: CSVRecord?) {
        BaseBar()
    }

    data class RequestParams(val f: String)

}