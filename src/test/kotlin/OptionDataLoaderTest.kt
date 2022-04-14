import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.eodhistoricaldata.options.DataLoaderOptionsEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.options.DataLoaderRequestOptionsEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class OptionDataLoaderTest () {

    val from = LocalDate.of(2022, 1, 1)
    val to = LocalDate.of(2022, 2, 1)

    @Autowired
    lateinit var victim : DataLoaderOptionsEOD

    @Test
    internal fun loadDefault() {
        val r = DataLoaderRequestOptionsEOD("MCD.US", "OeAFFmMliFG5orCUuwAKQ8l4WWFQ67YX", from, to)
        val data = victim.loadData(r);
        data.data.forEach { println(it) }
    }
}