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
        val stocks = listOf(
//            "SPY.US",
//            "AMC.US",
//            "AAPL.US",
//            "TSLA.US",
//            "QQQ.US",
            "IWM.US",
            "NIO.US",
            "AMD.US",
            "F.US",
            "PLTR.US",
            "BB.US",
            "WISH.US",
            "NVDA.US",
            "CLOV.US",
            "AMZN.US",
            "BAC.US",
            "XLF.US",
            "FB.US",
            "SNDL.US",
            "MSFT.US",
            "SPCE.US",
            "SNAP.US",
            "HYG.US",
            "WKHS.US",
            "EWZ.US",
            "BABA.US",
            "XLE.US",
            "TLRY.US",
            "BA.US",
            "EEM.US",
            "CLF.US",
            "NOK.US",
            "GE.US",
            "VXX.US",
            "GME.US",
            "AAL.US",
            "MU.US",
            "SOFI.US",
            "INTC.US",
            "CCIV.US",
            "VIAC.US",
            "RKT.US",
            "PLUG.US",
            "T.US",
            "GLD.US",
            "WFC.US",
            "CCL.US",
            "DKNG.US",
            "GDX.US",
            "X.US",
            "C.US",
            "XOM.US",
            "SQ.US",
            "JPM.US",
            "RIOT.US",
            "IVR.US",
            "GM.US",
            "NFLX.US",
            "ABNB.US",
            "UBER.US",
            "FCX.US",
            "TLT.US",
            "EFA.US",
            "RBLX.US",
            "ROKU.US",
            "FUBO.US",
            "TWTR.US",
            "VALE.US",
            "DIS.US",
            "SQQQ.US",
            "MARA.US",
            "ARKK.US",
            "JD.US",
            "PFE.US",
            "TQQQ.US",
            "TAL.US",
            "CLNE.US",
            "UVXY.US",
            "BBBY.US",
            "QS.US",
            "XPEV.US",
            "NKE.US",
            "FSR.US",
            "RIDE.US",
            "ATOS.US",
            "BIDU.US",
            "PBR.US",
            "SOS.US",
            "OCGN.US",
            "ITUB.US",
            "UWMC.US",
            "ET.US",
            "SENS.US",
            "MRNA.US",
            "CSCO.US",
            "MVIS.US",
            "CHWY.US"
        )

        stocks.forEach {
            val r = DataLoaderRequestEOD(it, "json", from, to, "d", "a")
            val d = victim.loadData(r)
            println(r.symbol + " " + d)
        }
    }
}