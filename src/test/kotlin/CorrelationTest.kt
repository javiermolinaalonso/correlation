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
    val to = LocalDate.of(2021, 12, 31)

    @Autowired
    lateinit var victim: CorrelationService

    @Autowired
    lateinit var dataLoader: DataLoaderDynamoDb

    @Test
    internal fun computeCorrelation() {
        val tickers = listOf("SPY.US",
        "QQQ.US")
        val intervals = listOf(7, 30, 60, 100, 200)
        val data = mutableMapOf<String, List<StockEOD>>()
        tickers.forEach { data[it] = dataLoader.loadData(DataLoaderRequestDynamoDb(it, from, to)) }

        from.datesUntil(to, Period.ofMonths(1)).forEachOrdered { date ->
            intervals.forEach {
                val result = victim.computeCorrelation(CorrelationRequest(data, date, it))
                result.data
                    .filter{it.value > 0.95 || it.value < -0.95}
                    .forEach { println(it) }
            }
        }
    }
}