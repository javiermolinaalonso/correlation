package com.javislaptop.correlation.service

import com.javislaptop.correlation.model.StockEOD
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CorrelationService {

    fun computeCorrelation(request: CorrelationRequest): CorrelationResponse {
        val map = request.data
        val correlationItems = mutableListOf<CorrelationItem>()
        map.forEach { (k, v) ->
            val values = getValues(v, request.date, request.items)
            map.filterNot { it.key == k }.forEach { (k1, v1) ->
                val values2 = getValues(v1, request.date, values.size)
                try {
                    val correlation = PearsonsCorrelation().correlation(values, values2)
                    correlationItems.add(CorrelationItem(request.date, correlation, values.size, k, k1))
                } catch (_: java.lang.Exception){

                }
            }
        }
        return CorrelationResponse(correlationItems)
    }

    private fun getValues(v: List<StockEOD>, date: LocalDate, items: Int) =
        v.filter { it.date <= date }.takeLast(items).map { it.close }.toDoubleArray()
}

data class CorrelationResponse(val data: List<CorrelationItem>)

data class CorrelationRequest(val data: Map<String, List<StockEOD>>, val date: LocalDate, val items: Int = 255)
