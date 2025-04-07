import java.io.File

data class StratGameSummary(
    val stratName : String,
    val averageCoinHistory: List<Double>,
    val coinHistoryVariance: List<Double>,
    val overallVariance: Double,
    val completionPercentage: Double,
    val averageCompletionRound: Int,
    val averageNonCompletionValue: Int
) {
    override fun toString(): String {
        return """
            StratGameSummary(
                stratName='$stratName',
                overallVariance=$overallVariance,
                completionPercentage=${"%.2f".format(completionPercentage * 100)}%,
                averageCompletionRound=$averageCompletionRound,
                averageNonCompletionValue=$averageNonCompletionValue,
            )
        """.trimIndent()
    }

    fun saveToCsv() {
        val file = File(OUTCOME_CSV_PATH).apply { parentFile.mkdirs(); createNewFile() }
        val existingLines = if (file.exists()) file.readLines().filterNot { it.startsWith("$stratName,") } else emptyList()
        if (existingLines.isEmpty()) {
            val header = "stratName,averageCoinHistory,coinHistoryVariance,overallVariance,completionPercentage," +
                    "averageCompletionRound,averageNonCompletionValue\n"
            file.writeText(header)
        } else {
            file.writeText(existingLines.joinToString ("\n") + "\n")
        }
        file.appendText(
            "$stratName," +
                    "\"${averageCoinHistory.joinToString(",")}\"," +
                    "\"${coinHistoryVariance.joinToString(",")}\"," +
                    "$overallVariance, " +
                    "$completionPercentage, " +
                    "$averageCompletionRound, " +
                    "$averageNonCompletionValue\n"
        )
        println("Saved summary for $stratName to $OUTCOME_CSV_PATH")
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
    val completionPercentage =(games.count { it.coins >= COIN_LIMIT }) / (games.size.toDouble())
    val averageCompletionRound = games
        .filter { it.coins >= COIN_LIMIT }
        .map { it.endRound }
        .average()
    val averageNonCompletionValue = games
        .filter { it.coins < COIN_LIMIT }
        .map { it.coins }
        .average()
    return StratGameSummary(
        stratName,
        averageCoinHistory,
        coinHistoryVariance,
        overallVariance,
        completionPercentage,
        averageCompletionRound.toInt(),
        averageNonCompletionValue.toInt()
    )
}