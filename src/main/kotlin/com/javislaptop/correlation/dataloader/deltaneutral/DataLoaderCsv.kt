package com.javislaptop.correlation.dataloader.deltaneutral

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

@Service
class DataLoaderCsv(val properties: DeltaNeutralProperties) {

    fun listFiles() = File(properties.basepath).walk().map { it.absolutePath }.filter { it.endsWith(".csv") }.toList()

    fun loadData(file: String) =
        CSVParser(
            FileReader(file),
            CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build()
        )
            .map { parseItem(it) }
            .toList()

    fun loadData(request: DataLoaderRequest): List<DeltaNeutralOption> {
        val filePath = properties.basepath + request.symbol + "/" + properties.fileprefix

        val csvFormat = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build()
        return request.from.datesUntil(request.to.plusDays(1), Period.ofDays(1))
            .flatMap {
                try {
                    CSVParser(
                        FileReader(filePath + it.format(DateTimeFormatter.ofPattern(properties.pattern)) + ".csv"),
                        csvFormat
                    )
                        .map { parseItem(it) }
                        .filter { item -> request.expiry?.isEqual(item.expiration) ?: true }
                        .filter { item -> request.type?.equals(item.type) ?: true }
                        .filter { item -> request.strike?.equals(item.strike) ?: true }
                        .stream()
                } catch (_: FileNotFoundException) {
                    Stream.empty()
                }
            }.toList()
    }

    private fun parseItem(it: CSVRecord) = DeltaNeutralOption(
        it["UnderlyingSymbol"],
        it["UnderlyingPrice"].toDouble(),
        getOptionSymbol(it),
        it["Type"],
        LocalDate.parse(it["DataDate"], DateTimeFormatter.ofPattern("MM/dd/yyyy")),
        LocalDate.parse(it["Expiration"], DateTimeFormatter.ofPattern("MM/dd/yyyy")),
        it["Strike"].toDouble(),
        it["Last"].toDouble(),
        it["Bid"].toDouble(),
        it["Ask"].toDouble(),
        it["Volume"].toDouble(),
        it["OpenInterest"].toInt()
    )

    fun getOptionSymbol(it : CSVRecord) : String{
        if (it.isMapped("OptionSymbol")) {
            return it["OptionSymbol"]
        }else {
            return it["OptionRoot"]
        }
    }
}

data class DataLoaderRequest(
    val symbol: String,
    val from: LocalDate,
    val to: LocalDate,
    val expiry: LocalDate? = null,
    val strike: Double? = null,
    val type: String? = null
)
