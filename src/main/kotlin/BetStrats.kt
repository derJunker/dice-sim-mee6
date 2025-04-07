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


val ACTIVE_STRATS: List<BettingStrategy> = listOf(PercentageBettingStrategy(0.5),
    PercentageBettingStrategy(0.3),
    PercentageBettingStrategy(0.05),
    PercentageBettingStrategy(0.02),
    PercentageBettingStrategy(0.035),
    PercentageBettingStrategy(0.1)
)
