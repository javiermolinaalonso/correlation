import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.model.StockEOD
import com.javislaptop.correlation.service.CorrelationRequest
import com.javislaptop.correlation.service.CorrelationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.Period

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class CorrelationTest {
    val from = LocalDate.of(2010, 1, 1)
    val to = LocalDate.of(2022, 3, 31)
    val SYMBOLS = listOf(
        "AMC",
        "AAPL",
        "TSLA",
        "BB",
        "AMZN",
        "MSFT",
        "BABA",
        "GE",
        "INTC",
        "T",
        "XOM",
        "RIOT",
        "GM",
        "NFLX",
        "DIS",
        "PFE",
        "QS",
        "NKE",
        "CSCO",
    )

    @Autowired
    lateinit var victim: CorrelationService

    @Autowired
    lateinit var dataLoader: DataLoaderDynamoDb

    @Test
    internal fun computeCorrelation() {
        val intervals = listOf(200)
        val data = mutableMapOf<String, List<StockEOD>>()
        SYMBOLS.forEach { data[it] = dataLoader.loadData(DataLoaderRequestDynamoDb(it, from, to)) }

        println("symbol1, symbol2, from, to, correlation, increaseSymbol1, increaseSymbol2")
        from.datesUntil(to, Period.ofMonths(1))
            .forEachOrdered { date ->
                intervals.forEach {
                    val result = victim.computeCorrelation(CorrelationRequest(data, date, it))
                    result.data
                        .filter { it.value > 0.95 || it.value < -0.95 }
                        .forEach { println(it) }
                }
            }
    }
}