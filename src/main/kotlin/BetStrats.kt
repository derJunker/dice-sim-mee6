open class BettingStrategy(
    val name: String,
    val getBetSize: (List<DiceRollOutcome>, List<Int>) -> Int,
)

private class PercentageBettingStrategy(
    private val percentage: Double,
) : BettingStrategy(
    "${percentage*100}%",
    { _, coinHistory -> (((coinHistory.last()-200) * percentage)+200).toInt() }
)

val FIFTY_PERC_STRAT: BettingStrategy = PercentageBettingStrategy(0.5)
val THIRTY_PERC_STRAT: BettingStrategy = PercentageBettingStrategy(0.3)
val FIVE_PERC_STRAT: BettingStrategy = PercentageBettingStrategy(0.05)
val TWO_PERC_STRAT: BettingStrategy = PercentageBettingStrategy(0.02)

val ACTIVE_STRATS: List<BettingStrategy> = listOf(FIFTY_PERC_STRAT, THIRTY_PERC_STRAT, FIVE_PERC_STRAT, TWO_PERC_STRAT)
