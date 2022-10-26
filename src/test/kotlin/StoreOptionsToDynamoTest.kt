import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.deltaneutral.DataLoaderCsv
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.datastore.StoreOptionsToDynamoDB
import com.javislaptop.correlation.datastore.StoreStockEodToDynamoDB
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.format.DateTimeFormatter

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class StoreOptionsToDynamoTest {

    @Autowired
    lateinit var victim : StoreOptionsToDynamoDB

    @Autowired
    lateinit var loader: DataLoaderCsv

    @Test
    internal fun storeTest() {
        victim.store();
    }

    @Test
    internal fun generateFileWithOptionsAndMonths() {
        var months = mutableSetOf<Pair<String, String>>()
        loader.listFiles()
            .forEach {
                val options = loader.loadData(it)
                months.add(Pair(options[0].underlyingSymbol, options[0].date.format(DateTimeFormatter.ofPattern("yyyyMM"))))
            }
        months.forEach {
            println(it)
        }
    }
}