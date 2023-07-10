import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class DataLoaderTest () {

    val from = LocalDate.of(2022, 1, 1)
    val to = LocalDate.of(2022, 2, 1)

    @Autowired
    lateinit var victim : DataLoaderEOD

    @Test
    internal fun loadDefault() {
        val stocks = listOf("SPY.US")

        stocks.forEach {
            val r = DataLoaderRequestEOD(it, "json", from, to, "d", "a")
            val d = victim.loadData(r)
            println(r.symbol + " " + d)
        }
    }
}