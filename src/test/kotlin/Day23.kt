import net.bytebuddy.description.type.TypeDefinition.Sort.describe
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

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
    }
})

fun parseNanobots(input: String) = input.split("\n").map { parseNanobot(it) }

fun parseNanobot(input: String): Nanobot {
    val regex = """pos=<(-?\d+),(-?\d+),(-?\d+)>,\s*r=(-?\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
    if (match.groupValues.size != 5) throw IllegalArgumentException("Only ${match.groupValues.size} elements parsed $input")
    val values = match.groupValues
    val coord = Coord3(values[1].toInt(), values[2].toInt(), values[3].toInt())
    return Nanobot(coord, values[4].toInt())
}



data class Nanobot(val coord: Coord3, val range: Int)

data class Coord3(val x: Int, val y: Int, val z: Int)
