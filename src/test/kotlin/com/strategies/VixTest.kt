package com.strategies

import com.fasterxml.jackson.databind.ObjectMapper
import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.io.FileReader
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.min

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class VixTest {
    val from = LocalDate.of(2000, 1, 1)
    val to = LocalDate.of(2023, 6, 1)

    @Autowired
    lateinit var victim: DataLoaderDynamoDb

    @Autowired
    lateinit var dynamoDbClient: DynamoDbClient

    @Test
    fun shortOpenLongCloseStrategy() {
        val SYMBOLS = listOf("SPY.US","AMC.US","AAPL.US","TSLA.US","QQQ.US","IWM.US","NIO.US","AMD.US","F.US","PLTR.US","BB.US","WISH.US","NVDA.US","CLOV.US","AMZN.US","BAC.US","XLF.US","FB.US","SNDL.US","MSFT.US","SPCE.US","SNAP.US","HYG.US","WKHS.US","EWZ.US","BABA.US","XLE.US","TLRY.US","BA.US","EEM.US","CLF.US","NOK.US","GE.US","VXX.US","GME.US","AAL.US","MU.US","SOFI.US","INTC.US","VIAC.US","RKT.US","PLUG.US","T.US","GLD.US","WFC.US","CCL.US","DKNG.US","GDX.US","X.US","C.US","XOM.US","SQ.US","JPM.US","RIOT.US","IVR.US","GM.US","NFLX.US","ABNB.US","UBER.US","FCX.US","TLT.US","EFA.US","RBLX.US","ROKU.US","FUBO.US","TWTR.US","VALE.US","DIS.US","SQQQ.US","MARA.US","ARKK.US","JD.US","PFE.US","TQQQ.US","TAL.US","CLNE.US","UVXY.US","BBBY.US","QS.US","XPEV.US","NKE.US","FSR.US","RIDE.US","ATOS.US","BIDU.US","PBR.US","SOS.US","OCGN.US","ITUB.US","UWMC.US","ET.US","SENS.US","MRNA.US","CSCO.US","MVIS.US","CHWY.US", "VIX.US")
        for (ticker in SYMBOLS) {
            val data = victim.loadData(DataLoaderRequestDynamoDb(ticker, from, to))
            var dollarsAmount = 0.0
            for (datum in data) {
                val shares = 1000.0 / datum.open
                val profit = shares * (datum.open - datum.close)
                dollarsAmount += profit
//            println("${datum.date}: $dollarsAmount")
            }
            println("Profit $ticker: $dollarsAmount")
        }
    }

    @Test
    fun name() {
        val data = victim.loadData(DataLoaderRequestDynamoDb("DIS.US", from, to))

        val differences = data.map {
            it.close - it.open
        }.toList()

        val reds = differences.count { it < 0 }
        val greens = differences.count { it > 0 }
        println("Reds: $reds. Green:$greens")

        val averageGreen = differences.filter { it > 0 }.average()
        val averageRed = differences.filter { it < 0 }.average()
        println("Avg red: $averageRed. Avg green: $averageGreen")

        val deviation = (averageGreen * greens + averageRed * reds) / differences.size
        println("Avg deviation: $deviation")
    }

    @Test
    internal fun pushDataToDynamo() {
        val reader = FileReader("/Users/javi/IdeaProjects/correlation/src/test/resources/vix.json");
        val mapper = ObjectMapper()
//        val value = mapper.readValue(reader, HashMap::class.java);

        val readTree = mapper.readTree(reader)
        val timestamps = readTree.get("chart").get("result")[0].get("timestamp")
        val opens = readTree.get("chart").get("result")[0].get("indicators").get("quote")[0].get("open")
        val closes = readTree.get("chart").get("result")[0].get("indicators").get("quote")[0].get("close")
        val highs = readTree.get("chart").get("result")[0].get("indicators").get("quote")[0].get("high")
        val lows = readTree.get("chart").get("result")[0].get("indicators").get("quote")[0].get("low")

        for (i in 0 until timestamps.size()) {
            val r = mutableMapOf<String, AttributeValue>()

            r["date"] = AttributeValue.fromS(
                LocalDate.ofInstant(
                    Instant.ofEpochSecond(timestamps.get(i).longValue()),
                    ZoneId.systemDefault()
                ).toString()
            )
            r["symbol"] = AttributeValue.fromS("VIX.US");
            r["open"] = AttributeValue.fromN(opens.get(i).doubleValue().toString())
            r["close"] = AttributeValue.fromN(closes.get(i).doubleValue().toString())
            r["high"] = AttributeValue.fromN(highs.get(i).doubleValue().toString())
            r["low"] = AttributeValue.fromN(lows.get(i).doubleValue().toString())
            r["volume"] = AttributeValue.fromN("0")
            dynamoDbClient.putItem(PutItemRequest.builder().tableName("StockEOD").item(r).build())
        }
        println("value")
//        dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder().requestItems(r).build())
    }
}