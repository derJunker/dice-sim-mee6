fun main() {
    val stratSummaries = mutableListOf<StratGameSummary>()
    for (strat in ACTIVE_STRATS) {
        println("Playing games with ${strat.name} strategy")
        val stratGames = mutableListOf<DiceGame>()
        for (gameNr in 1..GAMES_PLAYED) {
            val game = DiceGame(MAX_ROUNDS, strat)
            game.playGame()
            stratGames += game
        }
        stratSummaries.add(summaryOf(stratGames, strat))
    }
    println(stratSummaries)
    stratSummaries.forEach { it.saveToCsv() }
}