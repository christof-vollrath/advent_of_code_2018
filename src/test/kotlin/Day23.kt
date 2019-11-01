import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.jetbrains.spek.data_driven.data
import java.lang.Integer.max
import java.lang.Integer.min
import org.jetbrains.spek.data_driven.on as onData
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

/*
--- Day 23: Experimental Emergency Teleportation ---

Using your torch to search the darkness of the rocky cavern, you finally locate the man's friend: a small reindeer.

You're not sure how it got so far in this cave.
It looks sick - too sick to walk - and too heavy for you to carry all the way back.
Sleighs won't be invented for another 1500 years, of course.

The only option is experimental emergency teleportation.

You hit the "experimental emergency teleportation" button on the device
and push I accept the risk on no fewer than 18 different warning messages.
Immediately, the device deploys hundreds of tiny nanobots which fly around the cavern,
apparently assembling themselves into a very specific formation.
The device lists the X,Y,Z position (pos) for each nanobot as well
as its signal radius (r) on its tiny screen (your puzzle input).

Each nanobot can transmit signals to any integer coordinate which is a distance away from it less than or equal to its
signal radius (as measured by Manhattan distance).
Coordinates a distance away of less than or equal to a nanobot's signal radius are said to be in range of that nanobot.

Before you start the teleportation process, you should determine which nanobot is the strongest
(that is, which has the largest signal radius) and then, for that nanobot,
the total number of nanobots that are in range of it, including itself.

For example, given the following nanobots:

pos=<0,0,0>, r=4
pos=<1,0,0>, r=1
pos=<4,0,0>, r=3
pos=<0,2,0>, r=1
pos=<0,5,0>, r=3
pos=<0,0,3>, r=1
pos=<1,1,1>, r=1
pos=<1,1,2>, r=1
pos=<1,3,1>, r=1

The strongest nanobot is the first one (position 0,0,0) because its signal radius, 4 is the largest.
Using that nanobot's location and signal radius, the following nanobots are in or out of range:

The nanobot at 0,0,0 is distance 0 away, and so it is in range.
The nanobot at 1,0,0 is distance 1 away, and so it is in range.
The nanobot at 4,0,0 is distance 4 away, and so it is in range.
The nanobot at 0,2,0 is distance 2 away, and so it is in range.
The nanobot at 0,5,0 is distance 5 away, and so it is not in range.
The nanobot at 0,0,3 is distance 3 away, and so it is in range.
The nanobot at 1,1,1 is distance 3 away, and so it is in range.
The nanobot at 1,1,2 is distance 4 away, and so it is in range.
The nanobot at 1,3,1 is distance 5 away, and so it is not in range.

In this example, in total, 7 nanobots are in range of the nanobot with the largest signal radius.

Find the nanobot with the largest signal radius. How many nanobots are in range of its signals?

--- Part Two ---

Now, you just need to figure out where to position yourself so that you're actually teleported
when the nanobots activate.

To increase the probability of success, you need to find the coordinate which puts you in range
of the largest number of nanobots.
If there are multiple, choose one closest to your position (0,0,0, measured by manhattan distance).

For example, given the following nanobot formation:

pos=<10,12,12>, r=2
pos=<12,14,12>, r=2
pos=<16,12,12>, r=4
pos=<14,14,14>, r=6
pos=<50,50,50>, r=200
pos=<10,10,10>, r=5

Many coordinates are in range of some of the nanobots in this formation.
However, only the coordinate 12,12,12 is in range of the most nanobots: it is in range of the first five,
but is not in range of the nanobot at 10,10,10.
(All other coordinates are in range of fewer than five nanobots.)
This coordinate's distance from 0,0,0 is 36.

Find the coordinates that are in range of the largest number of nanobots.
What is the shortest manhattan distance between any of those points and 0,0,0?


 */

class Day23Spec : Spek({

    describe("part 1") {
        describe("parse nanobots") {
            val input = """
                pos=<0,0,0>, r=4
                pos=<1,0,0>, r=1
                pos=<4,0,0>, r=3
            """.trimIndent()
            it("should parse correctly") {
                val nanobots = parseNanobots(input)
                nanobots `should equal` listOf(
                        Nanobot(Coord3(0, 0, 0), 4),
                        Nanobot(Coord3(1, 0, 0), 1),
                        Nanobot(Coord3(4, 0, 0), 3)
                )
            }
        }
        given("example") {
           val input = """
                pos=<0,0,0>, r=4
                pos=<1,0,0>, r=1
                pos=<4,0,0>, r=3
                pos=<0,2,0>, r=1
                pos=<0,5,0>, r=3
                pos=<0,0,3>, r=1
                pos=<1,1,1>, r=1
                pos=<1,1,2>, r=1
                pos=<1,3,1>, r=1 
            """.trimIndent()
            val nanobots = parseNanobots(input)
            it("should find strongest nanobot") {
                nanobots.strongest() `should equal` Nanobot(Coord3(0, 0, 0), 4)
            }
            it("should find nanobots in range") {
                nanobots.inRangeOf(Nanobot(Coord3(0, 0, 0), 4)).map { it.coord } shouldContainSame listOf(
                        Coord3(0, 0, 0), Coord3(1, 0, 0), Coord3(4, 0, 0), Coord3(0, 2, 0),
                        Coord3(0, 0, 3), Coord3(1, 1, 1), Coord3(1, 1, 2)
                    )
            }
        }
        given("exercise") {
            val inputString = readResource("day23Input.txt")
            val nanobots = parseNanobots(inputString)
            val strongest = nanobots.strongest()
            it("should find strongest nanobot") {
                 strongest `should equal` Nanobot(coord=Coord3(x=43010063, y=90701411, z=15615412), range=99248181)
            }
            it("should find number of nanobots in range") {
                nanobots.inRangeOf(strongest!!).size `should equal` 408
            }
        }
    }
    describe("part 2") {
        given("two nanobots with overlapping ranges") {
            val input = """
                pos=<10,12,12>, r=2
                pos=<12,14,12>, r=2
            """.trimIndent()
            val nanobots = parseNanobots(input)
            it("should calculate range regions") {
                nanobots.rangeRegions() `should equal` listOf(RangeRegion(Coord3(8, 10, 10), Coord3(12, 14, 14)), RangeRegion(Coord3(10, 12, 10), Coord3(14, 16, 14)))
            }
            it("should calculate overlapping regions") {
                nanobots.rangeRegions().overlappingRegionsCount().toSet() `should equal` setOf (
                        RangeRegion(Coord3(10, 12, 10), Coord3(12, 14, 14)) to 2,
                        RangeRegion(Coord3(8, 10, 10), Coord3(12, 14, 14)) to 1,
                        RangeRegion(Coord3(10, 12, 10), Coord3(14, 16, 14)) to 1
                    )
            }
        }
        describe("sublists sequence") {
            given("inputs") {
                val testData = arrayOf(
                        data(emptyList<Int>(), setOf(emptyList<Int>())),
                        data(listOf(1), setOf(emptyList<Int>(), listOf(1))),
                        data(listOf(1, 2), setOf(emptyList<Int>(), listOf(1), listOf(1, 2), listOf(2))),
                        data(listOf(1, 2, 3), setOf(emptyList<Int>(), listOf(1), listOf(1, 2), listOf(1, 2, 3), listOf(1, 3), listOf(2), listOf(2, 3), listOf(3)))
                )
                onData("for %s it should bild results", with = *testData) { input, expected ->
                    val result= input.allSubListsAsSequence()
                    println(result.toList())
                    result.toSet() `should equal` expected
                }
            }
        }
        describe("overlapping regions in just one point") {
            given("two overlapping regions in one point") {
                val region1 = RangeRegion(Coord3(1, 1, 1), Coord3(3, 4, 5))
                val region2 = RangeRegion(Coord3(3, 4, 5), Coord3(4, 5, 6))
                on("calculate overlap") {
                    val overlappingRegion = region1.overlap(region2)
                    it("should find the overlapping point as a region with 0 size") {
                        overlappingRegion `should equal` RangeRegion(Coord3(3, 4, 5), Coord3(3, 4, 5))
                    }
                }
                on("calculate overlap with reversed regions") {
                    val overlappingRegion = region2.overlap(region1)
                    it("should find the same overlap") {
                        overlappingRegion `should equal` RangeRegion(Coord3(3, 4, 5), Coord3(3, 4, 5))
                    }
                }
            }
            given("two overlapping regions") {
                val region1 = RangeRegion(Coord3(1, 1, 1), Coord3(3, 4, 5))
                val region2 = RangeRegion(Coord3(2, 2, 2), Coord3(4, 5, 6))
                on("calculate overlap") {
                    val overlappingRegion = region1.overlap(region2)
                    it("should find the overlap") {
                        overlappingRegion `should equal` RangeRegion(Coord3(2, 2, 2), Coord3(3, 4, 5))
                    }
                }
                on("calculate overlap with reversed regions") {
                    val overlappingRegion = region2.overlap(region1)
                    it("should find the same overlap") {
                        overlappingRegion `should equal` RangeRegion(Coord3(2, 2, 2), Coord3(3, 4, 5))
                    }
                }
            }
        }
        describe("non overlapping regions") {
            given("two non overlapping regions") {
                val region1 = RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 1))
                val region2 = RangeRegion(Coord3(4, 1, 1), Coord3(5, 1, 1))
                on("calculate overlap") {
                    val overlappingRegion = region1.overlap(region2)
                    it("should find no overlap") {
                        overlappingRegion `should equal` null
                    }
                }
                on("calculate overlap with reversed regions") {
                    val overlappingRegion = region2.overlap(region1)
                    it("should find also no overlap") {
                        overlappingRegion `should equal` null
                    }
                }
            }
        }
        describe("compress range regions") {
            given("some range regions which can be compressed") {
                val regions = setOf(
                    RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 1)) to 1,
                    RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 2)) to 2,
                    RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 2)) to 1,
                    RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 1)) to 3,
                    null
                )
                on("compress") {
                    val result = regions.compress()
                    it("should compress regions to region with highest number") {
                        result `should equal` setOf(
                                RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 2)) to 2,
                                RangeRegion(Coord3(1, 1, 1), Coord3(3, 1, 1)) to 3,
                                null
                        )
                    }
                }
            }
        }
        describe("scale") {
            val input = """
                pos=<10,12,12>, r=2
                pos=<50,50,50>, r=200
            """.trimIndent()
            val regions = parseNanobots(input).rangeRegions()
            on("calculate scale and offset") {
                val offsetAndScale = regions.offsetAndScale(100.0)
                it("should have calculated correct scale and offset") {
                    offsetAndScale `should equal` (Coord3(150, 150, 150) to Scale3(0.25, 0.25, 0.25))
                }
                it("offset and scale") {
                    regions.map { it.offsetAndScale(offsetAndScale) } `should equal` listOf(
                            RangeRegion(topLeftFront=Coord3(x=40, y=40, z=40), bottomRightBack=Coord3(x=40, y=41, z=41)),
                            RangeRegion(topLeftFront=Coord3(x=0, y=0, z=0), bottomRightBack=Coord3(x=100, y=100, z=100))
                    )
                }
            }
            on("guess overlapping regions") {
                val overlappingRegions = regions.guessOverlappingRegions(51)
                it("should have guessed the overlapping region") {
                    overlappingRegions.first() `should equal` setOf(
                            RangeRegion(topLeftFront=Coord3(x=8, y=10, z=10), bottomRightBack=Coord3(x=12, y=14, z=14)),
                            RangeRegion(topLeftFront=Coord3(x=-150, y=-150, z=-150), bottomRightBack=Coord3(x=250, y=250, z=250))
                    )
                }
            }
        }
        given("example") {
            val input = """
                pos=<10,12,12>, r=2
                pos=<12,14,12>, r=2
                pos=<16,12,12>, r=4
                pos=<14,14,14>, r=6
                pos=<50,50,50>, r=200
                pos=<10,10,10>, r=5
            """.trimIndent()
            val nanobots = parseNanobots(input)
            it("should find the resulting coordinate by overlapping all regions - but this only works only with the exercise data") {
                nanobots.maxInRangeOverlappingAll()  `should equal`  Coord3(12, 12, 12)
            }
            it("should find the coord where most nanobots are in range") {
                nanobots.maxInRange() `should equal` Coord3(12, 12, 12)
            }
            it("should find all regions to combine to get the best region") {
                nanobots.rangeRegions().overlappingRegions().maxBy { it.second.size }!!.second.size `should equal` 5
            }
            it("should find the coord where most nanobots are in range in optimized way by first guessing interesting regions") {
                nanobots.maxInRangeOptimized() `should equal` Coord3(12, 12, 12)
            }
        }
        given("exercise") {
            val inputString = readResource("day23Input.txt")
            val nanobots = parseNanobots(inputString)
            it("should explore exercise nanobots") {
                nanobots.coords().filter { it > 0 }.max() `should equal` 223_126_255
                nanobots.coords().filter { it > 0 }.min() `should equal` 25_129
                nanobots.coords().filter { it < 0 }.max() `should equal` -120_483
                nanobots.coords().filter { it < 0 }.min() `should equal` -160_344_804
                nanobots.ranges().max() `should equal` 99_248_181
                nanobots.ranges().min() `should equal` 49_663_605
            }
            xit("should find the coord where most nanobots are in range") {
                nanobots.maxInRange() `should equal` Coord3(12, 12, 12)
            }
            it("should throw an illegal argument exception, because not all regions in the exercise overlap") {
                { nanobots.maxInRangeOverlappingAll() }  `should throw`  IllegalArgumentException::class
            }
            it("should find the coord where most nanobots are in range by first guessing") {
                val guess1 = nanobots.rangeRegions().guessOverlappingRegions(51).maxBy { it.size }!!
                println("size after guess 1 = ${guess1.size}")
                val guess2 = guess1.toList().guessOverlappingRegions(51).maxBy { it.size }!!
                println("size after guess 2 = ${guess2.size}")
                val result = guess2.toList().overlapAllRegions().selectCoord()
                println(result)
                println(Coord3(0, 0, 0) manhattanDistance result)
            }
        }
    }
})

private fun List<RangeRegion>.guessOverlappingRegions(gridSize: Int): List<Set<RangeRegion>> {
    val grid = Array(gridSize) {
        Array(gridSize) {
            Array(gridSize) { mutableSetOf<RangeRegion>() }
        }
    }
    val offsetAndScale = offsetAndScale((gridSize-1).toDouble())
    map { region ->
        region.offsetAndScale(offsetAndScale)
    }
    // Fill grid with all regions
    forEach { region ->
        val scaledRegion = region.offsetAndScale(offsetAndScale)
        (scaledRegion.topLeftFront.x..scaledRegion.bottomRightBack.x).map { x ->
            (scaledRegion.topLeftFront.y..scaledRegion.bottomRightBack.y).map { y ->
                (scaledRegion.topLeftFront.z..scaledRegion.bottomRightBack.y).map { z ->
                    grid[x][y][z].add(region)
                }
            }
        }
    }
    return sequence {
        grid.forEach { column ->
            column.forEach { row ->
                row.forEach { cell ->
                    if (cell.isNotEmpty()) yield (cell)
                }
            }
        }
    }.sortedByDescending { it.size }.toList()
}

private fun List<RangeRegion>.offsetAndScale(factor: Double): Pair<Coord3, Scale3> {
    val maxX = map { it.bottomRightBack.x }.max()!!
    val maxY = map { it.bottomRightBack.y }.max()!!
    val maxZ = map { it.bottomRightBack.z }.max()!!
    val minX = map { it.topLeftFront.x }.min()!!
    val minY = map { it.topLeftFront.y }.min()!!
    val minZ = map { it.topLeftFront.z }.min()!!
    val offsetX = -minX
    val offsetY = -minY
    val offsetZ = -minZ
    val scaleX = factor / (maxX - minX)
    val scaleY = factor / (maxY - minY)
    val scaleZ = factor / (maxZ - minZ)
    return Coord3(offsetX, offsetY, offsetZ) to Scale3(scaleX, scaleY, scaleZ)
}

private fun List<Nanobot>.coords() = flatMap { nanobot -> listOf(nanobot.coord.x, nanobot.coord.y, nanobot.coord.z) }
private fun List<Nanobot>.ranges() = map { nanobot -> nanobot.range }

private fun Set<Pair<RangeRegion, Int>?>.compress(): Set<Pair<RangeRegion, Int>?> =
        groupBy { it?.first }
        .map { (rangeRegion, regionsWithNr ) ->
            if (rangeRegion != null) regionsWithNr.filterNotNull().maxBy { regionWithNr -> regionWithNr.second }
            else null
        }
        .toSet()

fun List<Int>.allSubListsAsSequence(): Sequence<List<Int>> {
    fun merge(list: List<Int>?, e: Int?): List<Int>? =
            if (list != null && e != null) listOf(e) + list
            else if (e != null) listOf(e)
            else list
    return allSubListsAsSequence(::merge).map { it ?: emptyList<Int>() }
}

fun <E, M> List<E>.allSubListsAsSequence(merge: (M?, E?) -> M?): Sequence<M?> =
        sequence {
            println("allSubListsAsSequence list.size=${size}")
            if (isEmpty()) yield(null)
            else {
                drop(1).allSubListsAsSequence(merge).forEach {
                    yield(merge(it, null))
                    yield(merge(it, first()))
                }
            }
        }

fun List<Nanobot>.inRangeOf(nanobot: Nanobot) = filter { (coord, _) -> coord manhattanDistance nanobot.coord <= nanobot.range}
fun List<Nanobot>.strongest() = maxBy { it.range }
fun List<Nanobot>.maxInRange(): Coord3 = rangeRegions().overlappingRegionsCount().selectBestRegion().selectCoord()
fun List<Nanobot>.maxInRangeOverlappingAll(): Coord3 = rangeRegions().overlapAllRegions().selectCoord()
fun List<Nanobot>.maxInRangeOptimized(): Coord3 = rangeRegions().guessOverlappingRegions(101).maxBy { it.size }!!.toList().overlapAllRegions().selectCoord()
fun List<Nanobot>.rangeRegions()  = map { nanobot ->
    val coord = nanobot.coord
    val range = nanobot.range
    RangeRegion(Coord3(coord.x - range, coord.y - range, coord.z - range), Coord3(coord.x + range, coord.y + range, coord.z + range))
}

fun List<RangeRegion>.overlappingRegionsCount(): Sequence<Pair<RangeRegion, Int>> {
    fun mergeRegions(region1: Pair<RangeRegion, Int>?, region2: Pair<RangeRegion, Int>?): Pair<RangeRegion, Int>? =
        if (region1 != null && region2 != null) region1.overlap(region2)
        else region1 ?: region2
    val rangeRegionsWithNr = map { it -> it to 1 }
    return rangeRegionsWithNr.allSubListsAsSequence(::mergeRegions).filterNotNull()
}

fun List<RangeRegion>.overlappingRegions(): Sequence<Pair<RangeRegion, Set<RangeRegion>>> {
    fun mergeRegionWithRegions(regions: Pair<RangeRegion, Set<RangeRegion>>?, region: RangeRegion?): Pair<RangeRegion, Set<RangeRegion>>? {
        return if (region != null && regions != null) {
            val overlappingRegion = region.overlap(regions.first)
            if (overlappingRegion != null) overlappingRegion to regions.second + overlappingRegion
            else null
        }
        else if (region != null) region to setOf(region)
        else regions
    }
    return allSubListsAsSequence(::mergeRegionWithRegions).filterNotNull()
}

fun List<RangeRegion>.overlapAllRegions(): RangeRegion {
    val firstRegion = first()
    val nextRegions = drop(1)
    return nextRegions.fold(firstRegion) { res: RangeRegion, next: RangeRegion ->
        res.overlap(next) ?: res
    }
}

fun Pair<RangeRegion, Int>.overlap(with: Pair<RangeRegion, Int>): Pair<RangeRegion, Int>? {
    val overlappingRegion = first.overlap(with.first)
    return if (overlappingRegion == null) null
    else overlappingRegion to second + with.second
}

fun RangeRegion.overlap(with: RangeRegion): RangeRegion? {
    val topLeftFront = Coord3(
            max(topLeftFront.x, with.topLeftFront.x),
            max(topLeftFront.y, with.topLeftFront.y),
            max(topLeftFront.z, with.topLeftFront.z)
         )
    val bottomRightBack = Coord3(
            min(bottomRightBack.x, with.bottomRightBack.x),
            min(bottomRightBack.y, with.bottomRightBack.y),
            min(bottomRightBack.z, with.bottomRightBack.z)
    )
    return if (topLeftFront.x > bottomRightBack.x ||
                topLeftFront.y > bottomRightBack.y ||
                topLeftFront.z > bottomRightBack.z) null
    else RangeRegion(topLeftFront, bottomRightBack)
}
fun Sequence<Pair<RangeRegion, Int>>.selectBestRegion() = maxBy { it.second }!!.first

data class RangeRegion(val topLeftFront: Coord3, val bottomRightBack: Coord3) {
    fun selectCoord() = topLeftFront
    fun offsetAndScale(offsetAndScale: Pair<Coord3, Scale3>): RangeRegion {
        fun calculateUp(coord: Int, offset: Int, scale: Double) = floor((coord + offset) * scale).toInt()
        fun calculateDown(coord: Int, offset: Int, scale: Double) = ceil((coord + offset) * scale).toInt()
        val offset = offsetAndScale.first
        val scale = offsetAndScale.second
        return RangeRegion(Coord3(calculateDown(topLeftFront.x, offset.x, scale.x), calculateDown(topLeftFront.y, offset.y, scale.y), calculateDown(topLeftFront.z, offset.z, scale.z)),
                Coord3(calculateUp(bottomRightBack.x, offset.x, scale.x), calculateUp(bottomRightBack.y, offset.y, scale.y), calculateUp(bottomRightBack.z, offset.z, scale.z))
                )
    }
}

fun parseNanobots(input: String) = input.split("\n").map { parseNanobot(it) }

fun parseNanobot(input: String): Nanobot {
    val regex = """pos=<(-?\d+),(-?\d+),(-?\d+)>,\s*r=(-?\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
    require(match.groupValues.size == 5) { "Only ${match.groupValues.size} elements parsed $input" }
    val values = match.groupValues
    val coord = Coord3(values[1].toInt(), values[2].toInt(), values[3].toInt())
    return Nanobot(coord, values[4].toInt())
}

data class Nanobot(val coord: Coord3, val range: Int)

data class Coord3(val x: Int, val y: Int, val z: Int) {
    infix fun manhattanDistance(coord: Coord3) = (x - coord.x).absoluteValue + (y - coord.y).absoluteValue + (z - coord.z).absoluteValue
}

data class Scale3(val x: Double, val y: Double, val z: Double)
