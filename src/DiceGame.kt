import java.util.*

class DiceGame(
    private val maxRounds: Int,
) {
    var coins: Int = COIN_START
    var coinHistory = mutableListOf<Int>()
    var resultHistory = mutableListOf<DiceRollOutcome>()
    var endRound: Int = 0

    private fun doubleDiceRoll(): List<Int> {
        val random = Random()
        val dice1 = random.nextInt(6) + 1
        val dice2 = random.nextInt(6) + 1
        val dice3 = random.nextInt(6) + 1
        val dice4 = random.nextInt(6) + 1
        return listOf(dice1, dice2, dice3, dice4)
    }

    private fun determineOutcome(diceRolls: List<Int>): DiceRollOutcome {
        val opponentSum = diceRolls[0] + diceRolls[1]
        val playerSum = diceRolls[2] + diceRolls[3]
        val playerHasDouble = (diceRolls[2] == diceRolls[3])
        val playerHasDoubleSix = (diceRolls[2] == 6 && diceRolls[3] == 6)
        if (opponentSum > playerSum) {
            return DiceRollOutcome.LOSS
        } else if(playerHasDoubleSix) {
            return DiceRollOutcome.TRIPLE
        } else if (opponentSum < playerSum) {
            if (playerHasDouble) {
                return DiceRollOutcome.DOUBLE
            }
            return DiceRollOutcome.WIN
        } else {
            return DiceRollOutcome.DRAW
        }
    }

    private fun playRound() {
        val diceRolls = doubleDiceRoll()
        val outcome = determineOutcome(diceRolls)
        val betSize = (coins * 0.02).toInt()
        coins = when (outcome) {
            DiceRollOutcome.LOSS -> coins - betSize
            DiceRollOutcome.DRAW -> coins
            DiceRollOutcome.WIN -> coins + betSize
            DiceRollOutcome.DOUBLE -> coins + (betSize * 2)
            DiceRollOutcome.TRIPLE -> coins + (betSize * 3)
        }
        coinHistory.add(coins)
        resultHistory.add(outcome)
    }

    fun playGame() {
        for (round in 1..maxRounds) {
            coins += 200;
            if (coins > COIN_LIMIT) {
                endRound = round
                return
            }
            playRound()
        }
        endRound = maxRounds
    }
}