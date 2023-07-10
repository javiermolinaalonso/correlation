package com.javislaptop.correlation.ib

import com.ib.client.*
import com.ib.controller.ApiController
import com.ib.controller.ApiController.IConnectionHandler
import com.ib.controller.ApiController.IHistoricalDataHandler
import com.ib.controller.Bar
import java.time.LocalDate


class DataLoaderInteractiveBrokers(val ibClient: ApiController) {

    private val readerSignal: EReaderSignal? = null
    private val clientSocket: EClientSocket? = null
    protected var currentOrderId = -1


    fun loadData(request : DataLoaderRequestInteractiveBrokers) {
        val conId = 0
        val exchange = ""
        val currency = ""
        val tradingClass = ""
        val primaryExchange = ""
        val isin = ""
        val contract = Contract(
            conId,
            "AAPL",
            "STK",
            null,
            0.0,
            null,
            "1",
            exchange,
            currency,
            "AAPL",
            tradingClass,
            null,
            primaryExchange,
            false,
            "ISIN",
            isin,
            "",
            null
        );
        val handler = HistoricalHandler()
        ibClient.reqHistoricalData(contract, request.to.toString(), 200, Types.DurationUnit.DAY, Types.BarSize._1_day, Types.WhatToShow.BID_ASK, false, false, handler);
    }
    data class DataLoaderRequestInteractiveBrokers (val symbol: String, val from: LocalDate, val to: LocalDate)

    class HistoricalHandler : IHistoricalDataHandler {
        override fun historicalData(bar: Bar?) {
            TODO("Not yet implemented")
        }

        override fun historicalDataEnd() {
            TODO("Not yet implemented")
        }

    }
}