fun main() {
    val games = mutableListOf<DiceGame>()
    for (gameNr in 1..GAMES_PLAYED) {
        val game = DiceGame(MAX_ROUNDS, BettingStrategy("nothin") { _, _ -> 0 })
        game.playGame()
        games += game
    }
    val summary: OverallSummary = OverallSummary.of(games)
    summary.saveToCsv()
    println("Overall Summary: $summary")
}