import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.eodhistoricaldata.DataLoaderEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.DataLoaderRequestEOD
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class DataLoaderTest () {

    val from = LocalDate.of(2022, 1, 1)
    val to = LocalDate.of(2022, 2, 1)

    @Autowired
    lateinit var victim : DataLoaderEOD

    @Test
    internal fun loadDefault() {
        val r = DataLoaderRequestEOD("MCD.US", "OeAFFmMliFG5orCUuwAKQ8l4WWFQ67YX", "json", from, to, "d", "a")
        val data = victim.loadData(r);
        data.forEach { println(it) }
    }
}