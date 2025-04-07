import kotlin.concurrent.thread

fun main() {
    val stratSummaries = mutableListOf<StratGameSummary>()
        val threads = mutableListOf<Thread>()

    for (strat in ACTIVE_STRATS) {
            val thread = thread {
        val stratGames = mutableListOf<DiceGame>()
        for (gameNr in 1..GAMES_PLAYED) {
            val game = DiceGame(MAX_ROUNDS, strat)
            game.playGame()
            stratGames += game
        }
                val summary = summaryOf(stratGames, strat)
                synchronized(stratSummaries) {
                    stratSummaries.add(summary)
                }
    }
            threads.add(thread)
        }

        threads.forEach { it.join() }
    println(stratSummaries)
    stratSummaries.forEach { it.saveToCsv() }
}