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

--- Part Two ---

On the other hand, if the coordinates are safe,
maybe the best you can do is try to find a region near as many coordinates as possible.

For example, suppose you want the sum of the Manhattan distance to all of the coordinates to be less than 32.
For each location, add up the distances to all of the given coordinates; if the total of those distances is less than 32, 
that location is within the desired region. Using the same coordinates as above, the resulting region looks like this:

..........
.A........
..........
...###..C.
..#D###...
..###E#...
.B.###....
..........
..........
........F.

In particular, consider the highlighted location 4,3 located at the top middle of the region. 
Its calculation is as follows, where abs() is the absolute value function:

Distance to coordinate A: abs(4-1) + abs(3-1) =  5
Distance to coordinate B: abs(4-1) + abs(3-6) =  6
Distance to coordinate C: abs(4-8) + abs(3-3) =  4
Distance to coordinate D: abs(4-3) + abs(3-4) =  2
Distance to coordinate E: abs(4-5) + abs(3-5) =  3
Distance to coordinate F: abs(4-8) + abs(3-9) = 10
Total distance: 5 + 6 + 4 + 2 + 3 + 10 = 30

Because the total distance to all coordinates (30) is less than 32, the location is within the region.

This region, which also includes coordinates D and E, has a total size of 16.

Your actual region will need to be much larger than this example, though, 
instead including all locations with a total distance of less than 10000.

What is the size of the region containing all locations which have a total distance 
to all given coordinates of less than 10000?

 */

fun closeToMany(maxDist: Int, input: List<Pair<Int, Int>>): Int {
    val maxX = input.maxBy { it.first}?.first!!
    val maxY = input.maxBy { it.second}?.second!!
    val closeList = (1..maxX).flatMap { x ->
        (1..maxY).mapNotNull { y ->
            val sumDists = input.map { manhattanDistance(it, x to y)}.sum()
            if (sumDists < maxDist) sumDists else null
        }
    }
    return closeList.size
}

fun countFiniteAreas(closestMap: ChronalMap): Map<Int, Int> {
    val infiniteAreas = findInfiniteAreas(closestMap)
    val result = mutableMapOf<Int, Int>()
    closestMap.forEach { line ->
        line.forEach { v ->
            if (v != null) {
                val absValue = abs(v)
                if (absValue !in infiniteAreas) result[absValue] = (result[absValue]?:0) + 1
            }
        }
    }
    return result
}

fun findInfiniteAreas(closestMap: ChronalMap) = closestMap.mapIndexed { y, line ->
    when {
        y == 0 -> line
        y == closestMap.size -1 -> line
        else -> listOf(line.first(), line.last())
    }
}.flatten().mapNotNull { if (it == null) null else abs(it) }.toSet()

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
                val closests = inputMap.entries.minSetBy { (coord, _) -> manhattanDistance(coord, x to y) }
                if (closests.size == 1) -(closests.first().value)
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

fun List<Pair<Int, Int>>.findClosest(coord1: Pair<Int, Int>) = withIndex().minBy { (_, coord2) -> manhattanDistance(coord1, coord2)}!!.index

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
            val input = parseCoordinates(exampleInput)
            val chronalMap = chronalMap(input)

            describe("initialize map and print it") {

                chronalMap.toChronalString() `should equal` """
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
                val closestMap = chronalMap.findClosest(input)

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
            describe("find infinite areas") {
                val closestMap = chronalMap.findClosest(input)

                findInfiniteAreas(closestMap) `should equal` setOf(1, 2, 3, 6)
            }
            describe("count finite areas") {
                val closestMap = chronalMap.findClosest(input)

                countFiniteAreas(closestMap) `should equal` mapOf(4 to 9, 5 to 17)
            }
            describe("example result") {
                val closestMap = chronalMap.findClosest(input)

                countFiniteAreas(closestMap).values.max() `should equal` 17
            }

        }
        given("exercise") {
            val exerciseInput = readResource("day06Input.txt")
            val input = parseCoordinates(exerciseInput)
            val chronalMap = chronalMap(input)
            val closestMap = chronalMap.findClosest(input)
            countFiniteAreas(closestMap).values.max() `should equal` 3449
        }
    }
    describe("part 2") {
        given("example") {
            val exampleInput = """
                    1, 1
                    1, 6
                    8, 3
                    3, 4
                    5, 5
                    8, 9
                """.trimIndent()
            val input = parseCoordinates(exampleInput)
            
            it("should count close to many") {
                closeToMany(32, input) `should equal` 16
            }
        }
        given("exercise") {
            val exerciseInput = readResource("day06Input.txt")
            val input = parseCoordinates(exerciseInput)
            closeToMany(10000, input) `should equal` 44868
        }
    }

})
