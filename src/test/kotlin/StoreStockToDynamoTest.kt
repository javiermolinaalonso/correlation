import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.CorrelationMain.Companion.SYMBOLS
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.datastore.StoreStockEodToDynamoDB
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class StoreStockToDynamoTest {

    val bonds = listOf(
        "US10Y.GBOND",
        "US2Y.GBOND"
    )

    val from = LocalDate.of(2000, 1, 1)
    val to = LocalDate.of(2022, 12, 31)

    @Autowired
    lateinit var victim : StoreStockEodToDynamoDB

    @Test
    internal fun storeTest() {
        bonds.forEach {
            val r = DataLoaderRequestEOD(it, "json", from, to, "d", "a")
            victim.store(r);
        }
    }
}