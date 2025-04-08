import java.io.File

data class StratGameSummary(
    val stratName : String,
    val overallVariance: Double,
    val completionPercentage: Double,
    val averageCompletionRound: Int,
    val completionRoundVariance: Double,
    val averageNonCompletionValue: Int,
    val averageCoinHistory: List<Double>,
    val coinHistoryVariance: List<Double>
) {
    override fun toString(): String {
        return """
            StratGameSummary(
                stratName='$stratName',
                overallVariance=$overallVariance,
                completionPercentage=${"%.2f".format(completionPercentage * 100)}%,
                averageCompletionRound=$averageCompletionRound,
                completionRoundVariance=$completionRoundVariance,
                averageNonCompletionValue=$averageNonCompletionValue,
            )
        """.trimIndent()
    }

    fun saveToCsv() {
        val file = File(OUTCOME_CSV_PATH).apply { parentFile.mkdirs(); createNewFile() }
        val existingLines = if (file.exists()) file.readLines().filterNot { it.startsWith("$stratName,") } else emptyList()
        if (existingLines.isEmpty()) {
            val header = "stratName,overallVariance,completionPercentage,averageCompletionRound," +
                    "completionRoundVariance,averageNonCompletionValue," +
                    "averageCoinHistory,coinHistoryVariance\n"
            file.writeText(header)
        } else {
            file.writeText(existingLines.joinToString ("\n") + "\n")
        }
        file.appendText(
            "$stratName," +
                    "$overallVariance, " +
                    "$completionPercentage, " +
                    "$averageCompletionRound, " +
                    "$completionRoundVariance, " +
                    "$averageNonCompletionValue, " +
                    "\"${averageCoinHistory.joinToString(",")}\"," +
                    "\"${coinHistoryVariance.joinToString(",")}\"\n"
        )
        println("Saved summary for $stratName to $OUTCOME_CSV_PATH")
    }

    companion object {
        fun loadFromCsv(): List<StratGameSummary> {
            val file = File(OUTCOME_CSV_PATH)
            if (!file.exists()) return emptyList()
            return file.readLines().drop(1).map { line ->
                val parts = line.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                StratGameSummary(
                    parts[0].trim(),
                    parts[1].trim().toDouble(),
                    parts[2].trim().toDouble(),
                    parts[3].trim().toInt(),
                    parts[4].trim().toDouble(),
                    parts[5].trim().toInt(),
                    parts[6].trim().removeSurrounding("\"").split(",").map { it.toDouble() },
                    parts[7].trim().removeSurrounding("\"").split(",").map { it.toDouble() },
                )
            }
        }
    }
}

fun summaryOf(games: List<DiceGame>, strat: BettingStrategy): StratGameSummary {
    val stratName = strat.name
    val extendedCoinHistories = games
        .map { game -> game.coinHistory }
        .map { coinHistory -> coinHistory + List(MAX_ROUNDS - coinHistory.size) { coinHistory.last() } }
    val averageCoinHistory = List(MAX_ROUNDS) { index ->
        extendedCoinHistories
            .map { coinHistory -> coinHistory[index] }
            .average()
    }

    val coinHistoryVariance = List(MAX_ROUNDS) { index ->
        val mean = averageCoinHistory[index]
        extendedCoinHistories
            .map { coinHistory -> coinHistory[index] }
            .map { value -> (value - mean) * (value - mean) }
            .average()
    }
    val overallVariance = coinHistoryVariance.reduce { acc, variance -> acc + variance } / coinHistoryVariance.size

    val completedRounds = games
        .filter { it.coins >= COIN_LIMIT }
        .map { it.endRound }

    val completionPercentage = completedRounds.size / games.size.toDouble()
    val averageCompletionRound = completedRounds.average()
    val completionRoundVariance = completedRounds
        .map { (it - averageCompletionRound) * (it - averageCompletionRound) }
        .average()
    val averageNonCompletionValue = games
        .filter { it.coins < COIN_LIMIT }
        .map { it.coins }
        .average()
    return StratGameSummary(
        stratName,
        overallVariance,
        completionPercentage,
        averageCompletionRound.toInt(),
        completionRoundVariance,
        averageNonCompletionValue.toInt(),
        averageCoinHistory,
        coinHistoryVariance,
    )
}