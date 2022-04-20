import com.assets.options.entities.OptionBuilder
import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.deltaneutral.DataLoaderCsv
import com.javislaptop.correlation.dataloader.deltaneutral.DataLoaderRequest
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderEOD
import com.javislaptop.correlation.dataloader.eodhistoricaldata.stock.DataLoaderRequestEOD
import com.javislaptop.correlation.dataloader.options.DeltaNeutralConverter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class DeltaNeutralLoaderTest {

    val from = LocalDate.of(2019, 3, 5)
    val to = LocalDate.of(2019, 3, 5)

    @Autowired
    lateinit var victim: DataLoaderCsv

    @Autowired
    lateinit var converter: DeltaNeutralConverter

    @Test
    internal fun loadDefault() {
        val data = victim.loadData(DataLoaderRequest("T", from, to));
        data.forEach { println(it) }
    }

    @Test
    internal fun loadContract() {
        val calls = victim.loadData(DataLoaderRequest("T", from, to, LocalDate.of(2019, 3, 29), 30.0, "call"));
        calls.forEach {
            val call = OptionBuilder
                .create("T", it.underlyingPrice)
                .withBidAsk(it.bid, it.ask)
                .withCurrentDate(it.date)
                .withExpirationAt(it.expiration)
                .withStrikePrice(it.strike)
                .withRiskFree(0.0273)
                .buildCall()
            println(it)
            println(call)
        }
    }

    @Test
    internal fun listFilesTest() {
        victim.listFiles()
            .forEach {
                victim.loadData(it)
                    .map { converter.convert(it) }
                    .forEach { println(it) }
            }
    }
}