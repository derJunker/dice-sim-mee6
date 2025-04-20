import java.io.File

data class OverallSummary(
    val lossStreaks: Map<Int, Double>,
    val lossImbalances: Map<Int, Double>,
    val averageTimeBetweenDoublesOrTriples: Double,
    val averageTimeBetweenTriples: Double
) {
    fun saveToCsv() {
        val file = File(OVERALL_SUMMARY_CSV_PATH).apply { parentFile.mkdirs(); createNewFile() }
        val header = "lossStreaks,lossImbalances,averageTimeBetweenDoublesOrTriples,averageTimeBetweenTriples\n"
        file.writeText(header)
        file.appendText(
            "${lossStreaks.entries.joinToString(";") { "${it.key}:${it.value}" }}," +
                    "${lossImbalances.entries.joinToString(";") { "${it.key}:${it.value}" }}," +
                    "$averageTimeBetweenDoublesOrTriples," +
                    "$averageTimeBetweenTriples\n"
        )
    }

    override fun toString(): String {
        return """
            OverallSummary(
                lossStreaks=${lossStreaks.entries.map { "${it.key}:${formatPercentage(it.value)}" }},
                lossImbalances=${lossImbalances.entries.map { "${it.key}:${formatPercentage(it.value)}" }},
                averageTimeBetweenDoublesOrTriples=$averageTimeBetweenDoublesOrTriples,
                averageTimeBetweenTriples=$averageTimeBetweenTriples
            )
        """.trimIndent()
    }

    private fun formatPercentage(value: Double): String {
        if (value <= 0.0) return "0%"
        if (value >= 1.0) return "100%"

        val scaled = value * 100
        val str = "%.${10}f".format(scaled).trimEnd('0').trimEnd(',') // Keep high precision

        val result = StringBuilder()
        var nonZeroCount = 0
        var reachedDecimal = false

        for (ch in str) {
            if (ch == '.') {
                reachedDecimal = true
                result.append(ch)
            } else if (ch != '0') {
                result.append(ch)
                nonZeroCount++
            } else {
                // zero
                if (nonZeroCount > 0 || !reachedDecimal) {
                    result.append(ch)
                }
            }

            if (nonZeroCount == 2) break
        }

        // Fill in remaining digits after decimal if needed (for valid formatting)
        val output = result.toString().trimEnd('.')
        return "$output%"
    }

    companion object {

        fun loadFromCsv(): OverallSummary {
            val file = File(OVERALL_SUMMARY_CSV_PATH)
            if (!file.exists()) {
                throw IllegalStateException("File $OVERALL_SUMMARY_CSV_PATH does not exist.")
            }
            val lines = file.readLines()
            val data = lines[1].split(",")

            val lossStreaks = data[0].split(";").associate {
                val (key, value) = it.split(":")
                key.toInt() to value.toDouble()
            }
            val lossImbalances = data[1].split(";").associate {
                val (key, value) = it.split(":")
                key.toInt() to value.toDouble()
            }
            val averageTimeBetweenDoublesOrTriples = data[2].toDouble()
            val averageTimeBetweenTriples = data[3].toDouble()

            return OverallSummary(
                lossStreaks,
                lossImbalances,
                averageTimeBetweenDoublesOrTriples,
                averageTimeBetweenTriples
            )
        }


        fun of(games: List<DiceGame>): OverallSummary {
            val lossStreaksPerGame = mutableListOf<Map<Int, Double>>()
            val lossImbalancesPerGame = mutableListOf<Map<Int, Double>>()
            val timeBetweenDoublesOrTriplesPerGame = mutableListOf<Double>()
            val timeBetweenTriplesPerGame = mutableListOf<Double>()
            for (game in games) {
                val lossStreaks = mutableMapOf<Int, Int>()
                var currentLossStreak = 0
                var lastDoubleOrTriple = 0
                var lastTriple = 0
                val timeBetweenDoublesOrTriples = mutableListOf<Double>()
                val timeBetweenTriples = mutableListOf<Double>()
                val lossImbalanceOfLast50Games = mutableMapOf<Int, Int>()
                for ((index, round) in game.resultHistory.withIndex()) {
                    if (index >= 50) {
                        val last50Games = game.resultHistory.subList(index - 50, index)
                        val lossImbalance = last50Games.count {it == DiceRollOutcome.TRIPLE ||
                        it == DiceRollOutcome.DOUBLE || it == DiceRollOutcome.WIN } - last50Games.count{ it == DiceRollOutcome.LOSS }
                        lossImbalanceOfLast50Games[lossImbalance] = lossImbalanceOfLast50Games.getOrDefault(lossImbalance, 0) + 1
                    }
                    if (round == DiceRollOutcome.LOSS) {
                        currentLossStreak++
                    } else if (round == DiceRollOutcome.WIN || round == DiceRollOutcome.DOUBLE || round == DiceRollOutcome.TRIPLE) {
                        if (currentLossStreak > 0) {
                            lossStreaks[currentLossStreak] = lossStreaks.getOrDefault(currentLossStreak, 0) + 1
                            currentLossStreak = 0
                        }
                    }

                    if (round == DiceRollOutcome.DOUBLE || round == DiceRollOutcome.TRIPLE) {
                        if (lastDoubleOrTriple != 0) {
                            timeBetweenDoublesOrTriples.add(game.coinHistory.size - lastDoubleOrTriple.toDouble())
                        }
                        lastDoubleOrTriple = game.coinHistory.size
                    }
                    if (round == DiceRollOutcome.TRIPLE) {
                        if (lastTriple != 0) {
                            timeBetweenTriples.add(game.coinHistory.size - lastTriple.toDouble())
                        }
                        lastTriple = game.coinHistory.size
                    }
                }
                val averageTimeBetweenDoublesOrTriples = timeBetweenDoublesOrTriples.average()
                val averageTimeBetweenTriples = timeBetweenTriples.average()
                val totalStreaks = lossStreaks.values.reduce { acc, i -> acc+i }
                val percentageLossStreaks = mutableMapOf<Int, Double>()
                for (streak in lossStreaks) {
                    percentageLossStreaks[streak.key] = streak.value.toDouble() / totalStreaks
                }
                lossStreaksPerGame.add(percentageLossStreaks)

                val totalImbalances = lossImbalanceOfLast50Games.values.reduce { acc, i -> acc+i }
                val percentageLossImbalances = mutableMapOf<Int, Double>()
                for (imbalance in lossImbalanceOfLast50Games) {
                    percentageLossImbalances[imbalance.key] = imbalance.value.toDouble() / totalImbalances
                }
                lossImbalancesPerGame.add(percentageLossImbalances)

                timeBetweenDoublesOrTriplesPerGame.add(averageTimeBetweenDoublesOrTriples)
                timeBetweenTriplesPerGame.add(averageTimeBetweenTriples)
            }

            val lossStreaks = mutableMapOf<Int, Double>()
            for (i in 1 until  200) {
                val averageLossStreakOccurrence = lossStreaksPerGame.map { if(it[i] == null) 0.0 else it[i]!! }.average()
                if (averageLossStreakOccurrence > 0) {
                    lossStreaks[i] = averageLossStreakOccurrence
                }
            }
            val lossImbalances = mutableMapOf<Int, Double>()
            for (i in -100 until 100) {
                val averageLossImbalanceOccurrence = lossImbalancesPerGame.map { if(it[i] == null) 0.0 else it[i]!! }.average()
                if (averageLossImbalanceOccurrence > 0) {
                    lossImbalances[i] = averageLossImbalanceOccurrence
                }
            }

            return OverallSummary(
                lossStreaks = lossStreaks,
                lossImbalances = lossImbalances,
                averageTimeBetweenDoublesOrTriples = timeBetweenDoublesOrTriplesPerGame.average(),
                averageTimeBetweenTriples = timeBetweenTriplesPerGame.average(),
            )
        }
    }
}