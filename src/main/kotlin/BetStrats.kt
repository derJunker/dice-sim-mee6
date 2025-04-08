import java.util.*

open class BettingStrategy(
    val name: String,
    val getBetSize: (List<DiceRollOutcome>, List<Int>) -> Int,
) {
    fun playGames(): StratGameSummary {
        val stratGames = mutableListOf<DiceGame>()
        for (gameNr in 1..GAMES_PLAYED) {
            val game = DiceGame(MAX_ROUNDS, this)
            game.playGame()
            stratGames += game
        }
        return summaryOf(stratGames, this)
    }
}

class PercentageBettingStrategy(
    private val percentage: Double,
) : BettingStrategy(
    "%.6f".format(Locale.US, percentage * 100).trimEnd('0').trimEnd('.') + "%",
    { _, coinHistory -> (((coinHistory.last()-200) * percentage)+200).toInt() }
)

private val RANDOM_BETTING_STRAT = BettingStrategy(
    "Random"
) { _, coinHistory -> ((coinHistory.last() * (0.05 + Math.random() * 0.45))).toInt() }

private val DOUBLE_ON_LOSS = BettingStrategy(
    "Double on Loss"
) { outcomes, coinHistory ->
    if (outcomes.isEmpty()) return@BettingStrategy 200
    val lastOutcome = outcomes.last()
    val lastBet = coinHistory.last()
    if (lastOutcome == DiceRollOutcome.LOSS) {
        (lastBet * 2).toInt()
    } else {
        (lastBet * 0.5).toInt()
    }
}


val ACTIVE_STRATS: List<BettingStrategy> = listOf(
//    RANDOM_BETTING_STRAT,
//    DOUBLE_ON_LOSS,
//    PercentageBettingStrategy(0.05),
//    PercentageBettingStrategy(0.1),
//    PercentageBettingStrategy(0.125),
//    PercentageBettingStrategy(0.15),
//    PercentageBettingStrategy(0.2),
//    PercentageBettingStrategy(0.225),
//    PercentageBettingStrategy(0.25),
//    PercentageBettingStrategy(0.275),
//    PercentageBettingStrategy(0.3),
//    PercentageBettingStrategy(0.35),
//    PercentageBettingStrategy(0.4),
//    PercentageBettingStrategy(0.5),
)