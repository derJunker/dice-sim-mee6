import org.jetbrains.kotlinx.kandy.dsl.plot

fun main() {
    val summaries = StratGameSummary.loadFromCsv()
    plotAverageCoinHistories(summaries)
}

fun plotAverageCoinHistories(stratGameSummary: List<StratGameSummary>) {

}