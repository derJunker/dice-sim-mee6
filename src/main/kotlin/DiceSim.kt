import kotlin.concurrent.thread

fun main() {
    val stratSummaries = mutableListOf<StratGameSummary>()

    if (USE_MULTITHREADING) {
        val threads = mutableListOf<Thread>()

        for (strat in ACTIVE_STRATS) {
            val thread = thread {
                val summary = strat.playGames()
                synchronized(stratSummaries) {
                    stratSummaries.add(summary)
                }
            }
            threads.add(thread)
        }

        threads.forEach { it.join() }
    } else {
        for (strat in ACTIVE_STRATS) {
            val summary = strat.playGames()
            stratSummaries.add(summary)
        }
    }

    println(stratSummaries)
    stratSummaries.forEach { it.saveToCsv() }
}