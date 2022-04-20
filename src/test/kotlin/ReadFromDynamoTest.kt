import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class ReadFromDynamoTest {

    val from = LocalDate.of(2022, 1, 1)
    val to = LocalDate.of(2022, 2, 1)

    @Autowired
    lateinit var victim : DataLoaderDynamoDb

    @Test
    internal fun name() {
        val tickers = listOf("AAPL.US", "MSFT.US", "AMZN.US", "TSLA.US", "GOOGL.US", "NVDA.US", "BRKB.US", "FB.US", "UNH.US", "JNJ.US", "JPM.US", "V.US", "PG.US", "XOM.US", "HD.US", "CVX.US", "MA.US", "BAC.US", "ABBV.US", "PFE.US", "AVGO.US", "COST.US", "DIS.US", "KO.US")
        tickers.forEach {
            val d = victim.loadData(DataLoaderRequestDynamoDb(it, from, to))
            println(d)
        }
    }
}