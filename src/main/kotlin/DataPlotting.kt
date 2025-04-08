import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.*
import org.jetbrains.kotlinx.kandy.letsplot.layers.*
import org.jetbrains.kotlinx.kandy.letsplot.scales.guide.LegendType
import java.io.File

fun main() {
    val summaries = StratGameSummary.loadFromCsv()

    File("./lets-plot-images/${MAX_ROUNDS}x$GAMES_PLAYED").mkdirs()
    plotAverageCoinHistories(summaries)
    plotCompletionPercentage(summaries)
}

fun plotAverageCoinHistories(stratGameSummary: List<StratGameSummary>) {
    val coinHistoryData = mapOf(
        "averageCoinHistory" to stratGameSummary.map { it.averageCoinHistory }.flatten(),
        "index" to List(stratGameSummary.size) { 1..MAX_ROUNDS }.flatten(),
        "stratName" to stratGameSummary.map { strat -> List(MAX_ROUNDS) {strat.stratName} }.flatten()
    )
    coinHistoryData.plot {
        groupBy("stratName") {
            line {
                x("index") {
                    axis.name = "Round"
                }
                y("averageCoinHistory") {
                    axis.name = "Average Coins"
                }
                color("stratName")
            }
        }
    }.save("${MAX_ROUNDS}x$GAMES_PLAYED/averageCoinHistory.png")

}

fun plotCompletionPercentage(stratGameSummary: List<StratGameSummary>) {
    val completionData = mapOf(
        "completionPercentage" to stratGameSummary.map { it.completionPercentage },
        "averageCompletionRound" to stratGameSummary.map { it.averageCompletionRound },
        "completionRoundVariance" to stratGameSummary.map { it.completionRoundVariance },
        "stratName" to stratGameSummary.map { it.stratName }
    )
    completionData.plot {
        groupBy("stratName") {
            layout {
                 caption = "Completion Rounds By Completion Percentage after $MAX_ROUNDS Rounds"
            }
            points {
                x("completionPercentage") {
                    axis.name = "Completion Percentage"
                }
                y("averageCompletionRound") {
                    axis.name = "Average Completion Round"
                }
                color("stratName")
                size("completionRoundVariance") {
                    legend.type = LegendType.None
                    scale = continuous(3.0..7.0)
                }
            }
        }
    }.save("${MAX_ROUNDS}x$GAMES_PLAYED/completionPercentagePlot.png")
}
