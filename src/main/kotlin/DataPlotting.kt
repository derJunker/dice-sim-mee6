import org.jetbrains.kotlinx.kandy.dsl.plot

fun main() {
    val summaries = StratGameSummary.loadFromCsv()
    plotAverageCoinHistories(summaries.first())
}

fun plotAverageCoinHistories(stratGameSummary: StratGameSummary) {
    val xValues = (1..MAX_ROUNDS).toList()
    val yValues = stratGameSummary.averageCoinHistory
}