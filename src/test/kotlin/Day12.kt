import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 12: Subterranean Sustainability ---

The year 518 is significantly more underground than your history books implied.
Either that, or you've arrived in a vast cavern network under the North Pole.

After exploring a little, you discover a long tunnel that contains a row of small pots as far
as you can see to your left and right.
A few of them contain plants - someone is trying to grow things in these geothermally-heated caves.

The pots are numbered, with 0 in front of you. To the left, the pots are numbered -1, -2, -3, and so on;
to the right, 1, 2, 3....
Your puzzle input contains a list of pots from 0 to the right and whether they do (#)
or do not (.) currently contain a plant, the initial state. (No other pots currently contain plants.)
For example, an initial state of #..##.... indicates that pots 0, 3, and 4 currently contain plants.

Your puzzle input also contains some notes you find on a nearby table:
someone has been trying to figure out how these plants spread to nearby pots.
Based on the notes, for each generation of plants, a given pot has or does not have a plant based on
whether that pot (and the two pots on either side of it) had a plant in the last generation.
These are written as LLCRR => N, where L are pots to the left, C is the current pot being considered,
R are the pots to the right, and N is whether the current pot will have a plant in the next generation.

For example:

A note like ..#.. => . means that a pot that contains a plant but with no plants within two pots of it
will not have a plant in it during the next generation.
A note like ##.## => . means that an empty pot with two plants on each side of it
will remain empty in the next generation.
A note like .##.# => # means that a pot has a plant in a given generation if, in the previous generation,
there were plants in that pot, the one immediately to the left, and the one two pots to the right,
but not in the ones immediately to the right and two to the left.

It's not clear what these plants are for, but you're sure it's important,
so you'd like to make sure the current configuration of plants is sustainable
by determining what will happen after 20 generations.

For example, given the following input:

initial state: #..#.#..##......###...###

...## => #
..#.. => #
.#... => #
.#.#. => #
.#.## => #
.##.. => #
.#### => #
#.#.# => #
#.### => #
##.#. => #
##.## => #
###.. => #
###.# => #
####. => #

For brevity, in this example, only the combinations which do produce a plant are listed.
(Your input includes all possible combinations.)
Then, the next 20 generations will look like this:

                 1         2         3
       0         0         0         0
 0: ...#..#.#..##......###...###...........
 1: ...#...#....#.....#..#..#..#...........
 2: ...##..##...##....#..#..#..##..........
 3: ..#.#...#..#.#....#..#..#...#..........
 4: ...#.#..#...#.#...#..#..##..##.........
 5: ....#...##...#.#..#..#...#...#.........
 6: ....##.#.#....#...#..##..##..##........
 7: ...#..###.#...##..#...#...#...#........
 8: ...#....##.#.#.#..##..##..##..##.......
 9: ...##..#..#####....#...#...#...#.......
10: ..#.#..#...#.##....##..##..##..##......
11: ...#...##...#.#...#.#...#...#...#......
12: ...##.#.#....#.#...#.#..##..##..##.....
13: ..#..###.#....#.#...#....#...#...#.....
14: ..#....##.#....#.#..##...##..##..##....
15: ..##..#..#.#....#....#..#.#...#...#....
16: .#.#..#...#.#...##...#...#.#..##..##...
17: ..#...##...#.#.#.#...##...#....#...#...
18: ..##.#.#....#####.#.#.#...##...##..##..
19: .#..###.#..#.#.#######.#.#.#..#.#...#..
20: .#....##....#####...#######....#.#..##.

The generation is shown along the left, where 0 is the initial state.
The pot numbers are shown along the top, where 0 labels the center pot, negative-numbered pots extend to the left,
and positive pots extend toward the right.
Remember, the initial state begins at pot 0, which is not the leftmost pot used in this example.

After one generation, only seven plants remain.
The one in pot 0 matched the rule looking for ..#..,
the one in pot 4 matched the rule looking for .#.#., pot 9 matched .##.., and so on.

In this example, after 20 generations, the pots shown as # contain plants,
the furthest left of which is pot -2, and the furthest right of which is pot 34.
Adding up all the numbers of plant-containing pots after the 20th generation produces 325.

After 20 generations, what is the sum of the numbers of all pots which contain a plant?


To begin, get your puzzle input.

initial state: ##.#.####..#####..#.....##....#.#######..#.#...........#......##...##.#...####..##.#..##.....#..####

--- Part Two ---

You realize that 20 generations aren't enough.
After all, these plants will need to last another 1500 years to even reach your timeline, not to mention your future.

After fifty billion (50000000000) generations, what is the sum of the numbers of all pots which contain a plant?

 */

fun countPlants(plantState: List<Pair<Int, Boolean>>) = plantState.filter { it.second }.sumBy { it.first }

fun executePlantTransition(initialState: List<Pair<Int, Boolean>>, transitions: List<PlantTransition>): List<Pair<Int, Boolean>> {
    fun prefixList(nr: Int) = (nr-5 .. nr-1).map { it to false}
    fun suffixList(nr: Int) = (nr+1 .. nr+5).map { it to false}
    val expandedList = prefixList(initialState.first().first) + initialState + suffixList(initialState.last().first) // Make sure that list has enough empty pots at the beginning and the ending but not more
    return (2..(expandedList.size-3)).map { pos ->
        val subList = expandedList.subList(pos-2, pos+3).map { it.second }
        val transition = transitions.find { it.state == subList }
        if (transition == null) expandedList[pos].first to false
        else expandedList[pos].first to transition.nextState
    }
    .dropWhile { ! it.second }
    .dropLastWhile { ! it.second }
}

fun executePlantTransition(initialState: List<Pair<Int, Boolean>>, transitions: List<PlantTransition>, times: Int): List<Pair<Int, Boolean>> {
    var currentState = initialState
    repeat(times) {
        currentState = executePlantTransition(currentState, transitions)
    }
    return currentState
}

fun printPlantState(plantState: List<Pair<Int, Boolean>>) = plantState.map { if (it.second) '#' else '.' }.joinToString("")

fun parsePlantTransitions(transitionsString: String) = transitionsString.split("\n").map { parsePlantTransition(it) }

fun parsePlantTransition(transitionString: String): PlantTransition {
    val regex = """([.#]+) => ([.#])""".toRegex()
    val match = regex.find(transitionString) ?: throw IllegalArgumentException("Can not parse transition=$transitionString")
    if (match.groupValues.size != 3) throw IllegalArgumentException("Not all elements parsed, only ${match.groupValues.size}")
    val values = match.groupValues
    return PlantTransition(parseSinglePlantState(values[2][0]), parsePlantState(values[1]).map { it.second})
}

data class PlantTransition(val nextState: Boolean, val state: List<Boolean>)

fun parsePlantState(stateString: String) = stateString.mapIndexed { i, c -> i to parseSinglePlantState(c) }

fun parseSinglePlantState(c: Char): Boolean =when (c) {
    '.' -> false
    '#' -> true
    else -> throw IllegalArgumentException("Illegal char $c in state string")
}

class Day12Spec : Spek({

    describe("part 1") {
        given("example"){
            val initialStateString = "#..#.#..##......###...###"
            val transitionsString = """
                ...## => #
                ..#.. => #
                .#... => #
                .#.#. => #
                .#.## => #
                .##.. => #
                .#### => #
                #.#.# => #
                #.### => #
                ##.#. => #
                ##.## => #
                ###.. => #
                ###.# => #
                ####. => #
            """.trimIndent()

            describe("parse initial state") {
                it("should be parsed correctly") {
                    val parsedState = parsePlantState(initialStateString)
                    parsedState.size `should equal` 25
                    parsedState[0] `should equal` (0 to true)
                    parsedState[1] `should equal` (1 to false)
                }
            }
            describe("parse transition string") {
                given("a transitions string") {
                    val transitionString = "...## => #"
                    it("should be parsed correctly") {
                        parsePlantTransition(transitionString) `should equal` PlantTransition(true, listOf(false, false, false, true, true))
                    }
                }
            }
            describe("parse transition strings") {
                given("a transitions string") {
                    val transitions = parsePlantTransitions(transitionsString)
                    it("should be parsed correctly") {
                        transitions.size `should equal` 14
                        transitions[13] `should equal`  PlantTransition(true, listOf(true, true, true, true, false))
                    }
                }
            }
            describe("execute a single transition step") {
                given("an initial state and transitions") {
                    val initialState = parsePlantState("#..#.#..##......###...###")
                    val transitions = parsePlantTransitions(transitionsString)
                    it("should result the right next state") {
                        printPlantState(executePlantTransition(initialState, transitions)) `should equal` "#...#....#.....#..#..#..#"
                    }
                }
            }
            describe("execute multiple transition steps") {
                given("an initial state and transitions") {
                    val initialState = parsePlantState("#..#.#..##......###...###")
                    val transitions = parsePlantTransitions(transitionsString)
                    it("should result the right next state") {
                        printPlantState(executePlantTransition(initialState, transitions,  2)) `should equal` "##..##...##....#..#..#..##"
                        printPlantState(executePlantTransition(initialState, transitions, 3 )) `should equal` "#.#...#..#.#....#..#..#...#"
                        printPlantState(executePlantTransition(initialState, transitions, 20)) `should equal` "#....##....#####...#######....#.#..##"
                    }
                    it("should count plants") {
                        countPlants(executePlantTransition(initialState, transitions, 20)) `should equal` 325
                    }
                }
            }

        }
        given("exercise") {
            val input = readResource("day12Input.txt")
            val transitions = parsePlantTransitions(input)
            val initialState = parsePlantState("##.#.####..#####..#.....##....#.#######..#.#...........#......##...##.#...####..##.#..##.....#..####")
            it("should count plants") {
                countPlants(executePlantTransition(initialState, transitions, 20)) `should equal` 3798
            }
        }
    }
    describe("part 2") {
        given("exercise") {
            val input = readResource("day12Input.txt")
            val transitions = parsePlantTransitions(input)
            val initialState = parsePlantState("##.#.####..#####..#.....##....#.#######..#.#...........#......##...##.#...####..##.#..##.....#..####")
            val c1 = countPlants(executePlantTransition(initialState, transitions, 10000))
            it("should calculate diffs between cycles") {
                // it turns out that after some time the same pattern is just shifted and plant count increases by 780_000 after 10_000 cycles
                val c2 = countPlants(executePlantTransition(initialState, transitions, 20000))
                c2 `should equal` c1 + 780_000
            }
            it("should calculate number of plants after 50000000000 cycles") {
                val x = (50000000000L - 10_000) / 10_000
                val result = x * 780_000L + c1
                result `should equal` 3900000002212L
            }
        }
    }

})
