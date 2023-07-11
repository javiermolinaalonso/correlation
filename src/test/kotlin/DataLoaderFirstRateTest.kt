import com.javislaptop.correlation.dataloader.firstratedata.DataLoaderFirstRate
import org.junit.jupiter.api.Test
import org.ta4j.core.AnalysisCriterion
import org.ta4j.core.BarSeriesManager
import org.ta4j.core.BaseStrategy
import org.ta4j.core.Trade
import org.ta4j.core.analysis.criteria.ExpectancyCriterion
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
import org.ta4j.core.analysis.criteria.NumberOfPositionsCriterion
import org.ta4j.core.analysis.criteria.NumberOfWinningPositionsCriterion
import org.ta4j.core.analysis.criteria.pnl.AverageProfitCriterion
import org.ta4j.core.analysis.criteria.pnl.NetProfitCriterion
import org.ta4j.core.analysis.criteria.pnl.ProfitLossCriterion
import org.ta4j.core.analysis.criteria.pnl.ProfitLossPercentageCriterion
import org.ta4j.core.analysis.criteria.pnl.ProfitLossRatioCriterion
import org.ta4j.core.cost.CostModel
import org.ta4j.core.cost.LinearTransactionCostModel
import org.ta4j.core.cost.ZeroCostModel
import org.ta4j.core.indicators.DateTimeIndicator
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.Num
import org.ta4j.core.rules.TimeRangeRule
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class DataLoaderFirstRateTest {

    val victim: DataLoaderFirstRate = DataLoaderFirstRate()

    @Test
    fun name() {
        val data = victim.loadData(
            DataLoaderFirstRate.RequestParams(
                "/Users/javi/IdeaProjects/correlation/src/test/resources/VX_30min_sample.csv",
                Duration.of(30, ChronoUnit.MINUTES)
            )
        )

        val start = TimeRangeRule.TimeRange(LocalTime.of(5, 0), LocalTime.of(5, 30))
        val close = TimeRangeRule.TimeRange(LocalTime.of(15, 0), LocalTime.of(16, 0))
        val entryRule = TimeRangeRule(listOf(start), DateTimeIndicator(data))
        val exitRule = TimeRangeRule(listOf(close), DateTimeIndicator(data))

        val myStrategy = BaseStrategy(entryRule, exitRule)

        val manager = BarSeriesManager(data, LinearTransactionCostModel(0.00048), ZeroCostModel())
        val result = manager.run(myStrategy, Trade.TradeType.SELL, DecimalNum.valueOf(300))

//        result.positions.forEach {
//            println("type=${it.entry.type}, date=${data.getBar(it.entry.index).endTime}, price=${it.entry.pricePerAsset}")
//            println("type=${it.exit.type}, date=${data.getBar(it.exit.index).endTime}, price=${it.exit.pricePerAsset}")
//        }

        println("Positive trades: ${NumberOfWinningPositionsCriterion().calculate(data, result)}")
        println("Trades: ${NumberOfPositionsCriterion().calculate(data, result)}")
        println("Average Profit per trade: ${AverageProfitCriterion().calculate(data, result)}")
        println("Drawdown: ${MaximumDrawdownCriterion().calculate(data, result)}")
        println("Net result: ${ProfitLossCriterion().calculate(data, result)}")
        println("Ratio: ${ProfitLossRatioCriterion().calculate(data, result)}")
        println("Percentage: ${ProfitLossPercentageCriterion().calculate(data, result)}")
    }
}