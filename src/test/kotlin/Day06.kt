import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import kotlin.math.abs
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 6: Chronal Coordinates ---

The device on your wrist beeps several times, and once again you feel like you're falling.

"Situation critical," the device announces.
"Destination indeterminate. Chronal interference detected. Please specify new target coordinates."

The device then produces a list of coordinates (your puzzle input).
Are they places it thinks are safe or dangerous?
It recommends you check manual page 729. The Elves did not give you a manual.

If they're dangerous, maybe you can minimize the danger by finding the coordinate
that gives the largest distance from the other points.

Using only the Manhattan distance, determine the area around each coordinate
by counting the number of integer X,Y locations that are closest to that coordinate
(and aren't tied in distance to any other coordinate).

Your goal is to find the size of the largest area that isn't infinite.
For example, consider the following list of coordinates:

1, 1
1, 6
8, 3
3, 4
5, 5
8, 9

If we name these coordinates A through F, we can draw them on a grid, putting 0,0 at the top left:

..........
.A........
..........
........C.
...D......
.....E....
.B........
..........
..........
........F.

This view is partial - the actual grid extends infinitely in all directions.
Using the Manhattan distance, each location's closest coordinate can be determined, shown here in lowercase:

aaaaa.cccc
aAaaa.cccc
aaaddecccc
aadddeccCc
..dDdeeccc
bb.deEeecc
bBb.eeee..
bbb.eeefff
bbb.eeffff
bbb.ffffFf

Locations shown as . are equally far from two or more coordinates, and so they don't count as being closest to any.

In this example, the areas of coordinates A, B, C, and F are infinite - while not shown here,
their areas extend forever outside the visible grid.
However, the areas of coordinates D and E are finite: D is closest to 9 locations, and E is closest to 17
(both including the coordinate's location itself).
Therefore, in this example, the size of the largest area is 17.

What is the size of the largest area that isn't infinite?

 */

class Day06Spec : Spek({

    describe("part 1") {
        describe("manhattanDistance") {
            val testData = arrayOf(
                    data(Pair(1, 1), Pair(1, 1), 0),
                    data(Pair(1, 1), Pair(1, 2), 1),
                    data(Pair(1, 1), Pair(0, 1), 1),
                    data(Pair(1, 1), Pair(3, 4), 5),
                    data(Pair(1, 1), Pair(-5, -2), 9)
            )
            onData("manhattanDistance %s", with = *testData) { p1, p2, d ->
                val result = manhattanDistance(p1, p2)
                it("returns $d") {
                    result `should equal` d
                }
            }
        }
        describe("parse coordinates") {
            val input = """
                1, 1
                1, 6
                8, 3
            """.trimIndent()

            parseCoordinates(input) `should equal` listOf(
                    (1 to 1),
                    (1 to 6),
                    (8 to 3)
            )
        }
        describe("find min set, finding all minimal values") {
            val input = listOf(1, 2, 3, 2, 1)
            input.minSetBy { it } `should equal` setOf(1, 1)
        }
        given("example input") {
            val exampleInput = """
                    1, 1
                    1, 6
                    8, 3
                    3, 4
                    5, 5
                    8, 9
                """.trimIndent()

            describe("initialize map and print it") {
                val input = parseCoordinates(exampleInput)
                val map = chronalMap(input)

                map.toChronalString() `should equal` """
                        ..........
                        .A........
                        ..........
                        ........C.
                        ...D......
                        .....E....
                        .B........
                        ..........
                        ..........
                        ........F.
                        ..........

                    """.trimIndent()
            }
            describe("find closest coordinates and print it") {
                val input = parseCoordinates(exampleInput)
                val map = chronalMap(input)
                val closestMap = map.findClosest(input)

                closestMap.toChronalString() `should equal` """
                        aaaaa.cccc
                        aAaaa.cccc
                        aaaddecccc
                        aadddeccCc
                        ..dDdeeccc
                        bb.deEeecc
                        bBb.eeee..
                        bbb.eeefff
                        bbb.eeffff
                        bbb.ffffFf
                        bbb.ffffff

                    """.trimIndent()
            }
        }
        given("exercise") {
            val exerciseInput = readResource("day06Input.txt")


        }
    }
    describe("part 2") {

    }

})

private fun <T, R : Comparable<R>> Collection<T>.minSetBy(selector: (T) -> R): Set<T> {
    var result = mutableSetOf<T>()
    var minValue: R? = null
    forEach { e ->
        val value = selector(e)
        val currentMinValue = minValue
        if (currentMinValue == null) {
            minValue = value
            result.add(e)
        } else {
            if (value == currentMinValue) {
                result.add(e)
            } else if (value < currentMinValue) {
                minValue = value
                result = mutableSetOf(e)
            }
        }
    }
    return result
}

private fun ChronalMap.findClosest(input: List<Pair<Int, Int>>): ChronalMap {
    val inputMap = input.mapCoordinates()
    val result = mapIndexed { y, line ->
        line.mapIndexed { x, _ ->
            val v = inputMap[x to y]
            if (v != null) v
            else {
                val closests = inputMap.entries.minSetBy { (coord, i) -> manhattanDistance(coord, x to y) }
                if (closests.size == 1) -(closests.first()!!.value)
                else null
            }
        }
    }
    return result
}

typealias ChronalMap = List<List<Int?>>
// Assumptions: coordinates start with 1, coordinates are indexed starting with 1,
// owner ship by the negated index of the owning coordinate

fun chronalMap(input: List<Pair<Int, Int>>): ChronalMap {
    val maxX = input.maxBy { it.first}?.first!!
    val maxY = input.maxBy { it.second}?.second!!
    val inputMap = input.mapCoordinates()
    return (0..maxY+1).map { y ->
        (0..maxX+1).map {x ->
            inputMap[x to y]
        }
    }
}

fun List<Pair<Int, Int>>.mapCoordinates() = mapIndexed { index, pair -> pair to index + 1 }.toMap()

fun List<Pair<Int, Int>>.findClosest(coord1: Pair<Int, Int>) = withIndex().minBy { (i, coord2) -> manhattanDistance(coord1, coord2)}!!.index

fun ChronalMap.toChronalString() = map { line->
    line.map {
        if (it == null) '.'
        else {
            if (it < 0) 'a' + (-it).rem(26) - 1
            else 'A' + it.rem(26) - 1
        }
    }.joinToString("") + '\n'
}.joinToString("")



fun parseCoordinates(input: String) = input.split("\n")
        .map { line ->
            val nrs = line.split(",").map { it.trim().toInt() }
            nrs[0] to nrs[1]
        }

fun manhattanDistance(p1: Pair<Int, Int>, p2: Pair<Int, Int>) =
        abs(p1.first - p2.first) + abs(p1.second - p2.second)
