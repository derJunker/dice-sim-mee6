import kotlin.concurrent.thread

fun main() {
    val stratSummaries = mutableListOf<StratGameSummary>()
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
    println(stratSummaries)
    stratSummaries.forEach { it.saveToCsv() }
}