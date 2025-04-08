import kotlin.math.floor

fun main() {
    var currentPercentage = 0.1
    var lo = 0.05
    var hi = 0.15
    val firstSummary = PercentageBettingStrategy(currentPercentage).playGames()
    var currentScore = scoreOf(firstSummary)

    val iterations = 10
    for (i in 0 until iterations) {
        val left = (currentPercentage + lo) / 2
        val right = (currentPercentage + hi) / 2
        println("l: $left, curr: $currentPercentage ,r: $right")
        val leftSummary = PercentageBettingStrategy(left).playGames()
        val rightSummary = PercentageBettingStrategy(right).playGames()
        val leftScore = scoreOf(leftSummary)
        val rightScore = scoreOf(rightSummary)
        if (currentScore > leftScore && currentScore > rightScore) {
            lo = left
            hi = right
            println("Current (${currentPercentage}) score is best with: ${1/currentScore}")
        } else if (leftScore >= rightScore) {
            currentPercentage = left
            currentScore = leftScore
            hi = right
            println("left ($left) score is best with: ${1/currentScore}")
        } else if (rightScore > leftScore) {
            currentPercentage = right
            currentScore = rightScore
            lo = left
            println("right ($right) score is best with: ${1.0/currentScore}")
        }
    }
    println("After $iterations iterations, best percentage: $currentPercentage with score: ${1.0/currentScore}")
}

fun scoreOf(summary: StratGameSummary): Double {
    return  1.0 / summary.averageCompletionRound
}