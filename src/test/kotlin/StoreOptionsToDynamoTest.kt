import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.datastore.StoreOptionsToDynamoDB
import com.javislaptop.correlation.datastore.StoreStockEodToDynamoDB
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class StoreOptionsToDynamoTest {

    @Autowired
    lateinit var victim : StoreOptionsToDynamoDB

    @Test
    internal fun storeTest() {
        victim.store();
    }
}