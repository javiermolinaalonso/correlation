import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.CorrelationMain.Companion.SYMBOLS
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.datastore.StoreStockEodToDynamoDB
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class StoreStockToDynamoTest {

    val from = LocalDate.of(2000, 1, 1)
    val to = LocalDate.of(2022, 12, 31)

    @Autowired
    lateinit var victim : StoreStockEodToDynamoDB

    @Test
    internal fun storeTest() {
        CSVParser(
            FileReader("/Users/javi/IdeaProjects/correlation/src/test/resources/tickers.csv"),
            CSVFormat.DEFAULT
        ).stream().parallel().forEach {
            try {
                val r = DataLoaderRequestEOD(it[0], "json", from, to, "d", "a")
                println(LocalDateTime.now().toString() + " Processing " + it[0])
                victim.store(r);
            }catch (e : Exception) {
                println("Error processing ${e.cause}")
            }
        }
    }
}