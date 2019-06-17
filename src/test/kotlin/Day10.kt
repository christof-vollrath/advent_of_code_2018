import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 10: The Stars Align ---

It's no use; your navigation system simply isn't capable of providing walking directions in the arctic circle,
and certainly not in 1018.

The Elves suggest an alternative.
In times like these, North Pole rescue operations will arrange points of light in the sky
to guide missing Elves back to base.
Unfortunately, the message is easy to miss: the points move slowly enough that it takes hours to align them,
but have so much momentum that they only stay aligned for a second.
If you blink at the wrong time, it might be hours before another message appears.

You can see these points of light floating in the distance, and record their position in the sky and their velocity,
the relative change in position per second (your puzzle input). The coordinates are all given from your perspective;
given enough time, those positions and velocities will move the points into a cohesive message!

Rather than wait, you decide to fast-forward the process and calculate what the points will eventually spell.

For example, suppose you note the following points:

position=< 9,  1> velocity=< 0,  2>
position=< 7,  0> velocity=<-1,  0>
position=< 3, -2> velocity=<-1,  1>
position=< 6, 10> velocity=<-2, -1>
position=< 2, -4> velocity=< 2,  2>
position=<-6, 10> velocity=< 2, -2>
position=< 1,  8> velocity=< 1, -1>
position=< 1,  7> velocity=< 1,  0>
position=<-3, 11> velocity=< 1, -2>
position=< 7,  6> velocity=<-1, -1>
position=<-2,  3> velocity=< 1,  0>
position=<-4,  3> velocity=< 2,  0>
position=<10, -3> velocity=<-1,  1>
position=< 5, 11> velocity=< 1, -2>
position=< 4,  7> velocity=< 0, -1>
position=< 8, -2> velocity=< 0,  1>
position=<15,  0> velocity=<-2,  0>
position=< 1,  6> velocity=< 1,  0>
position=< 8,  9> velocity=< 0, -1>
position=< 3,  3> velocity=<-1,  1>
position=< 0,  5> velocity=< 0, -1>
position=<-2,  2> velocity=< 2,  0>
position=< 5, -2> velocity=< 1,  2>
position=< 1,  4> velocity=< 2,  1>
position=<-2,  7> velocity=< 2, -2>
position=< 3,  6> velocity=<-1, -1>
position=< 5,  0> velocity=< 1,  0>
position=<-6,  0> velocity=< 2,  0>
position=< 5,  9> velocity=< 1, -2>
position=<14,  7> velocity=<-2,  0>
position=<-3,  6> velocity=< 2, -1>

Each line represents one point. Positions are given as <X, Y> pairs:
X represents how far left (negative) or right (positive) the point appears,
while Y represents how far up (negative) or down (positive) the point appears.

At 0 seconds, each point has the position given. Each second, each point's velocity is added to its position.
So, a point with velocity <1, -2> is moving to the right, but is moving upward twice as quickly.
If this point's initial position were <3, 9>, after 3 seconds, its position would become <6, 3>.

Over time, the points listed above would move like this:

Initially:
........#.............
................#.....
.........#.#..#.......
......................
#..........#.#.......#
...............#......
....#.................
..#.#....#............
.......#..............
......#...............
...#...#.#...#........
....#..#..#.........#.
.......#..............
...........#..#.......
#...........#.........
...#.......#..........

After 1 second:
......................
......................
..........#....#......
........#.....#.......
..#.........#......#..
......................
......#...............
....##.........#......
......#.#.............
.....##.##..#.........
........#.#...........
........#...#.....#...
..#...........#.......
....#.....#.#.........
......................
......................

After 2 seconds:
......................
......................
......................
..............#.......
....#..#...####..#....
......................
........#....#........
......#.#.............
.......#...#..........
.......#..#..#.#......
....#....#.#..........
.....#...#...##.#.....
........#.............
......................
......................
......................

After 3 seconds:
......................
......................
......................
......................
......#...#..###......
......#...#...#.......
......#...#...#.......
......#####...#.......
......#...#...#.......
......#...#...#.......
......#...#...#.......
......#...#..###......
......................
......................
......................
......................

After 4 seconds:
......................
......................
......................
............#.........
........##...#.#......
......#.....#..#......
.....#..##.##.#.......
.......##.#....#......
...........#....#.....
..............#.......
....#......#...#......
.....#.....##.........
...............#......
...............#......
......................
......................

After 3 seconds, the message appeared briefly: HI.
Of course, your message will be much longer and will take many more seconds to appear.

What message will eventually appear in the sky?
 */

data class Velocity(val dx: Int, val dy: Int)
data class Position(val x: Int, val y: Int)
data class LightPoint(var position: Position, val velocity: Velocity)

class FloatingLightPoints(val lightPoints: List<LightPoint>) {
    val minX = lightPoints.map { it.position.x }.min()!!
    val maxX = lightPoints.map { it.position.x }.max()!!
    val array: List<List<Char>> by lazy {
        lightPointsToArray(lightPoints, minX, maxX)
    }

    private fun lightPointsToArray(lightPoints: List<LightPoint>, minX: Int, maxX: Int): List<List<Char>> {
        val maxY = lightPoints.map { it.position.y }.max()!!
        val minY = lightPoints.map { it.position.y }.min()!!
        val lightPointsMap = lightPoints.map { it.position to it}.toMap()
        return (minY..maxY).map { y ->
            (minX..maxX).map { x ->
                if (Position(x, y) in lightPointsMap) '#'
                else '.'
            }
        }
    }
}

fun findMessage(initLightPoints: FloatingLightPoints): Pair<FloatingLightPoints, Int> {
    var nr = 0
    var lightPoints = initLightPoints
    while (! detectMessage(lightPoints)) {
        nr++
        lightPoints = moveLightPoints(lightPoints)
    }
    return lightPoints to nr
}

fun detectMessage(lightPoints: FloatingLightPoints): Boolean {
    if (arrayToHigh(lightPoints, 100)) return false // Height of text will not be higher than 100
    val pointsArray = lightPoints.array
    val size = pointsArray.size
    val columnDistribution = calculateDistribution(pointsArray)
    val count0 = columnDistribution.filter { it == 0 }.size
    val countFull = columnDistribution.filter { it == size }.size
    return count0 > size * 0.03 && countFull > size * 0.01 // Some assumption about distribution of points in a text
}

fun arrayToHigh(lightPoints: FloatingLightPoints, maxHight: Int): Boolean {
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE
    lightPoints.lightPoints.forEach { lightPoint ->
        if (lightPoint.position.y < minY) minY = lightPoint.position.y
        if (lightPoint.position.y > maxY) maxY = lightPoint.position.y
        if (maxY - minY > maxHight) {
            return true
        }
    }
    return false
}

fun calculateDistribution(pointsArray: List<List<Char>>): List<Int> {
    val sizeX = pointsArray[0].size // Assuming every row has same size
    return (0 until sizeX).map {x ->
        pointsArray.map { line ->
            if (line[x] == '#') 1
            else 0
        }.sum()
    }
}

fun moveLightPoints(lightPoints: FloatingLightPoints): FloatingLightPoints {
    val nextPoints = lightPoints.lightPoints.map { lightPoint ->
        val position = movePosition(lightPoint.position, lightPoint.velocity)
        LightPoint(position, lightPoint.velocity)
    }
    return FloatingLightPoints(nextPoints)
}

fun movePosition(position: Position, velocity: Velocity) =
        Position(position.x + velocity.dx, position.y + velocity.dy)


fun printLightPoints(lightPoints: FloatingLightPoints): String {
    return lightPoints.array.joinToString("\n") { line ->
        line.joinToString("")
    }
}


fun parsePositionVelocityLines(input: String): FloatingLightPoints {
    val lines = input.split("\n")
    val lightPointList =  lines.filter { it.isNotBlank() }.map {
        parsePositionVelocityLine(it)
    }
    return FloatingLightPoints(lightPointList)
}

fun parsePositionVelocityLine(line: String): LightPoint {
    val regex = """position=<\s*(-?\d+),\s*(-?\d+)> velocity=<\s*(-?\d+),\s*(-?\d+)>""".toRegex()
    val match = regex.find(line) ?: throw IllegalArgumentException("Can not parse line=$line")
    if (match.groupValues.size != 5) throw IllegalArgumentException("Not all elements parsed")
    val values = match.groupValues
    return LightPoint(Position(values[1].toInt(), values[2].toInt()), Velocity(values[3].toInt(), values[4].toInt()))
}

class Day10Spec : Spek({

    describe("part 1 and 2") {
        given("example") {
            val input = """
                position=< 9,  1> velocity=< 0,  2>
                position=< 7,  0> velocity=<-1,  0>
                position=< 3, -2> velocity=<-1,  1>
                position=< 6, 10> velocity=<-2, -1>
                position=< 2, -4> velocity=< 2,  2>
                position=<-6, 10> velocity=< 2, -2>
                position=< 1,  8> velocity=< 1, -1>
                position=< 1,  7> velocity=< 1,  0>
                position=<-3, 11> velocity=< 1, -2>
                position=< 7,  6> velocity=<-1, -1>
                position=<-2,  3> velocity=< 1,  0>
                position=<-4,  3> velocity=< 2,  0>
                position=<10, -3> velocity=<-1,  1>
                position=< 5, 11> velocity=< 1, -2>
                position=< 4,  7> velocity=< 0, -1>
                position=< 8, -2> velocity=< 0,  1>
                position=<15,  0> velocity=<-2,  0>
                position=< 1,  6> velocity=< 1,  0>
                position=< 8,  9> velocity=< 0, -1>
                position=< 3,  3> velocity=<-1,  1>
                position=< 0,  5> velocity=< 0, -1>
                position=<-2,  2> velocity=< 2,  0>
                position=< 5, -2> velocity=< 1,  2>
                position=< 1,  4> velocity=< 2,  1>
                position=<-2,  7> velocity=< 2, -2>
                position=< 3,  6> velocity=<-1, -1>
                position=< 5,  0> velocity=< 1,  0>
                position=<-6,  0> velocity=< 2,  0>
                position=< 5,  9> velocity=< 1, -2>
                position=<14,  7> velocity=<-2,  0>
                position=<-3,  6> velocity=< 2, -1>
            """.trimIndent()

            it("should parse points") {
                val lightPoints = parsePositionVelocityLines(input)
                lightPoints.lightPoints[0] `should equal` LightPoint(Position(9, 1), Velocity(0, 2))
                lightPoints.lightPoints[30] `should equal` LightPoint(Position(-3, 6), Velocity(2, -1))
                lightPoints.lightPoints.size `should equal` 31
            }
            it("should print light points") {
                val lightPoints = parsePositionVelocityLines(input)
                printLightPoints(lightPoints) `should equal` """
                    ........#.............
                    ................#.....
                    .........#.#..#.......
                    ......................
                    #..........#.#.......#
                    ...............#......
                    ....#.................
                    ..#.#....#............
                    .......#..............
                    ......#...............
                    ...#...#.#...#........
                    ....#..#..#.........#.
                    .......#..............
                    ...........#..#.......
                    #...........#.........
                    ...#.......#..........
                """.trimIndent()
            }
            it("should move light points") {
                val lightPoints = parsePositionVelocityLines(input)
                val movedLightPoints = moveLightPoints(lightPoints)
                printLightPoints(movedLightPoints) `should equal` """
                    ........#....#....
                    ......#.....#.....
                    #.........#......#
                    ..................
                    ....#.............
                    ..##.........#....
                    ....#.#...........
                    ...##.##..#.......
                    ......#.#.........
                    ......#...#.....#.
                    #...........#.....
                    ..#.....#.#.......
                """.trimIndent()
            }
            it("after 3 seconds hi should appear") {
                val lightPoints = parsePositionVelocityLines(input)
                var movedLightPoints = lightPoints
                repeat(3) { movedLightPoints = moveLightPoints(movedLightPoints) }
                printLightPoints(movedLightPoints) `should equal` """
                    #...#..###
                    #...#...#.
                    #...#...#.
                    #####...#.
                    #...#...#.
                    #...#...#.
                    #...#...#.
                    #...#..###
                """.trimIndent()
            }
            it("detect message") {
                val lightPoints = parsePositionVelocityLines(input)
                val (result, nr) = findMessage(lightPoints)
                nr `should equal` 3
                printLightPoints(result) `should equal` """
                    #...#..###
                    #...#...#.
                    #...#...#.
                    #####...#.
                    #...#...#.
                    #...#...#.
                    #...#...#.
                    #...#..###
                """.trimIndent()
            }
        }
        given("exercise") {
            val input = readResource("day10Input.txt")
            it("should find message") {
                val lightPoints = parsePositionVelocityLines(input)
                val (result, nr) = findMessage(lightPoints)
                nr `should equal` 10304
                printLightPoints(result) `should equal` """
                    #####.....##....#....#..#.......#####.....##....#####...#####.
                    #....#...#..#...##...#..#.......#....#...#..#...#....#..#....#
                    #....#..#....#..##...#..#.......#....#..#....#..#....#..#....#
                    #....#..#....#..#.#..#..#.......#....#..#....#..#....#..#....#
                    #####...#....#..#.#..#..#.......#####...#....#..#####...#####.
                    #.......######..#..#.#..#.......#.......######..#.......#..#..
                    #.......#....#..#..#.#..#.......#.......#....#..#.......#...#.
                    #.......#....#..#...##..#.......#.......#....#..#.......#...#.
                    #.......#....#..#...##..#.......#.......#....#..#.......#....#
                    #.......#....#..#....#..######..#.......#....#..#.......#....#
                """.trimIndent()

            }
        }
    }
})
