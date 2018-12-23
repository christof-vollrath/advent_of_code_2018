import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 9: Marble Mania ---

You talk to the Elves while you wait for your navigation system to initialize.
To pass the time, they introduce you to their favorite marble game.

The Elves play this game by taking turns arranging the marbles in a circle according to very particular rules.
The marbles are numbered starting with 0 and increasing by 1 until every marble has a number.

First, the marble numbered 0 is placed in the circle.
At this point, while it contains only a single marble, it is still a circle:
the marble is both clockwise from itself and counter-clockwise from itself.
This marble is designated the current marble.

Then, each Elf takes a turn placing the lowest-numbered remaining marble
into the circle between the marbles that are 1 and 2 marbles clockwise of the current marble.
(When the circle is large enough, this means that there is one marble between the marble
that was just placed and the current marble.)
The marble that was just placed then becomes the current marble.

However, if the marble that is about to be placed has a number which is a multiple of 23,
something entirely different happens.
First, the current player keeps the marble they would have placed, adding it to their score.
In addition, the marble 7 marbles counter-clockwise from the current marble is removed from the circle
and also added to the current player's score.
The marble located immediately clockwise of the marble that was removed becomes the new current marble.

For example, suppose there are 9 players.
After the marble with value 0 is placed in the middle, each player (shown in square brackets) takes a turn.
The result of each of those turns would produce circles of marbles like this,
where clockwise is to the right and the resulting current marble is in parentheses:

[-] (0)
[1]  0 (1)
[2]  0 (2) 1
[3]  0  2  1 (3)
[4]  0 (4) 2  1  3
[5]  0  4  2 (5) 1  3
[6]  0  4  2  5  1 (6) 3
[7]  0  4  2  5  1  6  3 (7)
[8]  0 (8) 4  2  5  1  6  3  7
[9]  0  8  4 (9) 2  5  1  6  3  7
[1]  0  8  4  9  2(10) 5  1  6  3  7
[2]  0  8  4  9  2 10  5(11) 1  6  3  7
[3]  0  8  4  9  2 10  5 11  1(12) 6  3  7
[4]  0  8  4  9  2 10  5 11  1 12  6(13) 3  7
[5]  0  8  4  9  2 10  5 11  1 12  6 13  3(14) 7
[6]  0  8  4  9  2 10  5 11  1 12  6 13  3 14  7(15)
[7]  0(16) 8  4  9  2 10  5 11  1 12  6 13  3 14  7 15
[8]  0 16  8(17) 4  9  2 10  5 11  1 12  6 13  3 14  7 15
[9]  0 16  8 17  4(18) 9  2 10  5 11  1 12  6 13  3 14  7 15
[1]  0 16  8 17  4 18  9(19) 2 10  5 11  1 12  6 13  3 14  7 15
[2]  0 16  8 17  4 18  9 19  2(20)10  5 11  1 12  6 13  3 14  7 15
[3]  0 16  8 17  4 18  9 19  2 20 10(21) 5 11  1 12  6 13  3 14  7 15
[4]  0 16  8 17  4 18  9 19  2 20 10 21  5(22)11  1 12  6 13  3 14  7 15
[5]  0 16  8 17  4 18(19) 2 20 10 21  5 22 11  1 12  6 13  3 14  7 15
[6]  0 16  8 17  4 18 19  2(24)20 10 21  5 22 11  1 12  6 13  3 14  7 15
[7]  0 16  8 17  4 18 19  2 24 20(25)10 21  5 22 11  1 12  6 13  3 14  7 15

The goal is to be the player with the highest score after the last marble is used up.
Assuming the example above ends after the marble numbered 25,
the winning score is 23+9=32 (because player 5 kept marble 23 and removed marble 9,
while no other player got any points in this very short example game).

Here are a few more examples:

10 players; last marble is worth 1618 points: high score is 8317
13 players; last marble is worth 7999 points: high score is 146373
17 players; last marble is worth 1104 points: high score is 2764
21 players; last marble is worth 6111 points: high score is 54718
30 players; last marble is worth 5807 points: high score is 37305

What is the winning Elf's score?

To begin, get your puzzle input.
435 players; last marble is worth 71184 points

*/

class Day09Spec : Spek({

    describe("part 1") {
        describe("play examples") {
            val testData = arrayOf(
                    data(0, ElvesPlayState(mutableListOf(0), 0)),
                    data(1, ElvesPlayState(mutableListOf(0, 1), 1, 1)),
                    data(2, ElvesPlayState(mutableListOf(0, 2, 1), 1, 2)),
                    data(3, ElvesPlayState(mutableListOf(0, 2, 1, 3), 3, 3)),
                    data(22, ElvesPlayState(mutableListOf(0, 16, 8, 17, 4, 18, 9, 19, 2, 20, 10, 21, 5, 22, 11, 1, 12, 6, 13, 3, 14, 7, 15), 13, 4)),
                    data(23, ElvesPlayState(mutableListOf(0, 16, 8, 17, 4, 18, 19, 2, 20, 10, 21, 5, 22, 11, 1, 12, 6, 13, 3, 14, 7, 15), 6, 5, mutableMapOf(5 to 32)))
            )
            onData("play step %s", with = *testData) { nr, state ->
                val result = elvesPlay(nr, 9)
                it("returns $state") {
                    result `should equal` state
                }
            }
        }
        describe("more examples") {
            val testData = arrayOf(
                    data(10, 1618, 8317),
                    data(13, 7999, 146373),
                    data(17, 1104, 2764),
                    data(21, 6111, 54718),
                    data(30, 5807, 37305)
            )
            onData("play %s", with = *testData) { nrPlayers, nrMarbles, highScore ->
                val result = elvesPlay(nrMarbles, nrPlayers).playersMap.values.max()
                it("returns $highScore") {
                    result `should equal` highScore
                }
            }
        }
        describe("exercise") {
            val result = elvesPlay(71184, 435).playersMap.values.max()
            result `should equal` 412959
        }
    }
})

fun elvesPlay(nr: Int, players: Int): ElvesPlayState = ElvesPlayState(mutableListOf(0), 0).also { state ->
    for (i in 1..nr) {
        state.currentPlayer = (state.currentPlayer ?: 0) % players + 1
        if (i % 23 == 0) {
            val removePos = with(state.currentMarble - 7) {
                if (this > 0) this
                else state.marbles.size - 7 + state.currentMarble
            }
            state.apply {
                val removedMarble = marbles[removePos]
                marbles.removeAt(removePos)
                currentMarble = removePos
                playersMap[currentPlayer!!] = playersMap.getOrDefault(currentPlayer!!, 0) + i + removedMarble
            }
        } else {
            val insertPos = (state.currentMarble + 1) % state.marbles.size + 1
            state.apply {
                marbles.add(insertPos, i)
                currentMarble = insertPos
            }
        }
    }
}

data class ElvesPlayState(val marbles: MutableList<Int>, var currentMarble: Int, var currentPlayer: Int? = null, val playersMap: MutableMap<Int, Int> = mutableMapOf())
