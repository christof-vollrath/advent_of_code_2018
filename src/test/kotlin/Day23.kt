import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldContainSame
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.jetbrains.spek.data_driven.data
import java.lang.Integer.max
import java.lang.Integer.min
import org.jetbrains.spek.data_driven.on as onData
import kotlin.math.absoluteValue
import kotlin.math.pow

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
                nanobots.rangeRegions().overlappingRegions() `should equal` setOf (
                        RangeRegion(Coord3(10, 12, 10), Coord3(12, 14, 14)) to 2,
                        RangeRegion(Coord3(8, 10, 10), Coord3(12, 14, 14)) to 1,
                        RangeRegion(Coord3(10, 12, 10), Coord3(14, 16, 14)) to 1
                    )
            }
        }
        describe("subsets") {
            given("inputs") {
                val testData = arrayOf(
                        data(emptySet<Int>(), setOf(emptySet<Int>())),
                        data(setOf(1), setOf(emptySet<Int>(), setOf(1))),
                        data(setOf(1, 2), setOf(emptySet<Int>(), setOf(1), setOf(1, 2), setOf(2))),
                        data(setOf(1, 2, 3), setOf(emptySet<Int>(), setOf(1), setOf(1, 2), setOf(1, 2, 3), setOf(1, 3), setOf(2), setOf(2, 3), setOf(3)))
                )
                onData("for %s it should bild results", with = *testData) { input, expected ->
                    input.allSubSets() `should equal` expected
                }
            }
            given("set with 10 elements") {
                val set10 = List(10) { it + 1}.toSet()
                it("should create all sub sets which has size of 2^10") {
                    val subSets = set10.allSubSets()
                    subSets.size `should equal` (2.0).pow(10).toInt()
                }
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
                    input.allSubLists().toSet() `should equal` expected
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
            it("should find the coord where most nanobots are in range") {
                nanobots.maxInRange() `should equal` Coord3(12, 12, 12)
            }
        }
        given("exercise") {
            val inputString = readResource("day23Input.txt")
            val nanobots = parseNanobots(inputString)
            it("should find the coord where most nanobots are in range") {
                nanobots.maxInRange() `should equal` Coord3(12, 12, 12)
            }
        }
    }
})

private fun Set<Pair<RangeRegion, Int>?>.compress(): Set<Pair<RangeRegion, Int>?> =
        groupBy { it?.first }
        .map { (rangeRegion, regionsWithNr ) ->
            if (rangeRegion != null) regionsWithNr.filterNotNull().maxBy { regionWithNr -> regionWithNr.second }
            else null
        }
        .toSet()

fun <E> List<E>.allSubLists(): Sequence<List<E>> =
    sequence {
        if (this@allSubLists.isEmpty()) yield(emptyList<E>())
        else {
            drop(1).allSubLists().forEach {
                yield(it)
                yield(listOf(first()) + it)
            }
        }
    }

fun <E> Set<E>.allSubSets(): Set<Set<E>> {
    fun merge(set: Set<E>?, e: E): Set<E>? =
            if (set == null) setOf(e)
            else set + e

    val resultIncludingNull = allSubSets(::merge)
    return resultIncludingNull.map { it ?: emptySet<E>() }.toSet()
}

fun <E, M> Set<E>.allSubSets(merge: (M?, E) -> M, compress: (Set<M?>) -> Set<M?> = { it }): Set<M?> {
    fun subSets(list: List<E>): Set<M?> {
        println("sublists list.size=${list.size}")
        if (list.isEmpty()) return setOf(null)
        else {
            val first = list.first()
            val subLists = subSets(list.drop(1))
            println("allSubSets list.size=${list.size} subLists.size=${subLists.size}")
            //println("allSubSets list=${list} subLists=${subLists}")
            return compress(subLists.map { merge(it, first) }.toSet() + subLists)
        }
    }
    return subSets(toList())
}

fun List<Nanobot>.inRangeOf(nanobot: Nanobot) = filter { (coord, _) -> coord manhattanDistance nanobot.coord <= nanobot.range}
fun List<Nanobot>.strongest() = maxBy { it.range }
fun List<Nanobot>.maxInRange(): Coord3 = rangeRegions().overlappingRegions().selectBestRegion().selectCoord()
fun List<Nanobot>.rangeRegions()  = map { nanobot ->
    val coord = nanobot.coord
    val range = nanobot.range
    RangeRegion(Coord3(coord.x - range, coord.y - range, coord.z - range), Coord3(coord.x + range, coord.y + range, coord.z + range))
}
fun List<RangeRegion>.overlappingRegions(): Set<Pair<RangeRegion, Int>> {
    fun mergeRegions(region1: Pair<RangeRegion, Int>?, region2: Pair<RangeRegion, Int>): Pair<RangeRegion, Int>? {
        return if (region1 == null) region2
        else region1.overlap(region2)
    }
    fun compressRegions(regions: Set<Pair<RangeRegion, Int>?>) = regions.compress()
    val rangeRegionsWithNr = map { it -> it to 1 }.toSet()
    return rangeRegionsWithNr.allSubSets(::mergeRegions, ::compressRegions).filterNotNull().toSet()
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

fun Set<Pair<RangeRegion, Int>>.selectBestRegion() = maxBy { it.second }!!.first

data class RangeRegion(val topLeftFront: Coord3, val bottomRightBack: Coord3) {
    fun selectCoord() = Coord3(12, 12, 12)
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
