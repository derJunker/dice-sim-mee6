fun main() {
    for (gameNr in 1..GAMES_PLAYED) {
        val game = DiceGame(MAX_ROUNDS)
        game.playGame()
        if (game.coins >= COIN_LIMIT) {
            println("Game $gameNr: Won after ${game.endRound} rounds with ${game.coins} coins.")
            println("Coin history: ${game.coinHistory}")
        } else {
            println("Game $gameNr: Lost after ${game.endRound} rounds with ${game.coins} coins.")
            println("Result history: ${game.resultHistory}")
        }
    }
}