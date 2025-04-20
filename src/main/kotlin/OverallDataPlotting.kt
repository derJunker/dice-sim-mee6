import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.*
import org.jetbrains.kotlinx.kandy.letsplot.layers.*
import org.jetbrains.kotlinx.kandy.letsplot.scales.guide.LegendType
import org.jetbrains.kotlinx.kandy.letsplot.settings.Symbol
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.util.color.Color
import kotlin.math.abs

fun main() {
    var summary = OverallSummary.loadFromCsv()
    plotLossImbalances(summary)
    plotCumulativeLossImbalances(summary)
}

fun plotLossImbalances(summary: OverallSummary) {
    plot {
        bars {
            x(summary.lossImbalances.keys) {
                axis.name = "Imbalance"
            }
            y(summary.lossImbalances.values.map { prob -> prob * 100 }) {
                axis.name = "Probability of Imbalance (%)"
            }
        }
    }.save("lossImbalances${MAX_ROUNDS}x$GAMES_PLAYED.png")
}

fun plotCumulativeLossImbalances(summary: OverallSummary) {
    val sortedEntries = summary.lossImbalances.filter { entry -> abs(entry.key) < 23 }.toSortedMap()
    val xValues = sortedEntries.keys.toList()
    val yValues = sortedEntries.values

    // Compute cumulative sum
    val cumulative = mutableListOf<Double>()
    var sum = 0.0
    for (value in yValues) {
        sum += value
        cumulative.add(sum * 100) // convert to percentage
    }

    plot {
        x(xValues, name = "Imbalance") {
            axis.name = "Imbalance"
        }

        step {
            y(cumulative)
            color = Color.LIGHT_GREEN
        }
        points {
            y(cumulative, name = "Probability of having an imbalance of x or less")
            symbol = Symbol.CIRCLE_SMALL
            color = Color.GREEN
        }

        layout {
            title = "Cumulative Probability of Imbalance"
        }
    }.save("cumulativeLossImbalances${MAX_ROUNDS}x$GAMES_PLAYED.png")
}