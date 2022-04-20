import com.assets.options.entities.OptionBuilder
import com.javislaptop.correlation.CorrelationMain
import com.javislaptop.correlation.dataloader.deltaneutral.DataLoaderCsv
import com.javislaptop.correlation.dataloader.deltaneutral.DataLoaderRequest
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderDynamoDb
import com.javislaptop.correlation.dataloader.dynamodb.stock.DataLoaderRequestDynamoDb
import com.javislaptop.correlation.dataloader.options.DeltaNeutralConverter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.abs

@SpringBootTest(classes = arrayOf(CorrelationMain::class))
class OptionsPlaybook {

    val from = LocalDate.of(2019, 3, 1)
    val to = LocalDate.of(2019, 3, 29)

    @Autowired
    lateinit var optionsLoader: DataLoaderCsv

    @Autowired
    lateinit var stockLoader: DataLoaderDynamoDb

    @Autowired
    lateinit var deltaNeutralConverter: DeltaNeutralConverter

    @Test
    internal fun foo() {
        val request = DataLoaderRequest("T", from, to, LocalDate.of(2019, 3, 29), null, null)
        val loadedOptions = optionsLoader.loadData(request);
        val stockData = stockLoader.loadData(DataLoaderRequestDynamoDb("T.US", from, to))
        val options = deltaNeutralConverter.convert(loadedOptions).groupBy { it.currentDate }

        val interestingOptions = stockData
            .filter { it.date.dayOfWeek == DayOfWeek.MONDAY }
            .flatMap { stock ->
                val closePrice =
                    options[stock.date]?.minByOrNull { abs(it.strikePrice.toDouble() - stock.close) }?.strikePrice
                options[stock.date]
                    ?.filter { it.strikePrice.equals(closePrice) }
                    ?.filter { it.expirationDate.isEqual(to) }
                    .orEmpty()
            }

        interestingOptions.forEach {
            println(it)
        }

        val optSymbols = interestingOptions.map { it.optionSymbol }.toList()
        options[to]
            ?.filter { optSymbols.contains(it.optionSymbol) }
            ?.forEach {
                println(it)
            }

        //74$ Loss at T190329X00030000
        //44$ Prof at T190329X00031000

    }

}