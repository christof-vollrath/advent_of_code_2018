import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import java.lang.Math.abs
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 25: Four-Dimensional Adventure ---

The reindeer's symptoms are getting worse, and neither you nor the white-bearded man have a solution.
At least the reindeer has a warm place to rest: a small bed near where you're sitting.

As you reach down, the reindeer looks up at you, accidentally bumping a button on your wrist-mounted device
with its nose in the process - a button labeled "help".

"Hello, and welcome to the Time Travel Support Hotline! If you are lost in time and space, press 1.
If you are trapped in a time paradox, press 2. If you need help caring for a sick reindeer, press 3. If you--"

Beep.

A few seconds later, you hear a new voice. "Hello; please state the nature of your reindeer."
You try to describe the situation.

"Just a moment, I think I can remotely run a diagnostic scan."
A beam of light projects from the device and sweeps over the reindeer a few times.

"Okay, it looks like your reindeer is very low on magical energy;
it should fully recover if we can fix that. Let me check your timeline for a source.... Got one.
There's actually a powerful source of magical energy about 1000 years forward from you,
and at roughly your position, too! It looks like... hot chocolate?
Anyway, you should be able to travel there to pick some up; just don't forget a mug!
Is there anything else I can help you with today?"

You explain that your device isn't capable of going forward in time. "I... see. That's tricky.
Well, according to this information, your device should have the necessary hardware to open a small portal
and send some hot chocolate back to you. You'll need a list of fixed points in spacetime; I'm transmitting it to you now."

"You just need to align your device to the constellations of fixed points so that it can lock on to the destination
and open the portal. Let me look up how much hot chocolate that breed of reindeer needs."

"It says here that your particular reindeer is-- this can't be right, it says there's only one like that in the universe!
But THAT means that you're--" You disconnect the call.

The list of fixed points in spacetime (your puzzle input) is a set of four-dimensional coordinates.
To align your device, acquire the hot chocolate, and save the reindeer,
you just need to find the number of constellations of points in the list.

Two points are in the same constellation if their manhattan distance apart is no more than 3
or if they can form a chain of points, each a manhattan distance no more than 3 from the last,
between the two of them.
(That is, if a point is close enough to a constellation, it "joins" that constellation.)

 For example:

 0,0,0,0
 3,0,0,0
 0,3,0,0
 0,0,3,0
 0,0,0,3
 0,0,0,6
 9,0,0,0
12,0,0,0

In the above list, the first six points form a single constellation: 0,0,0,0 is exactly distance 3 from the next four,
and the point at 0,0,0,6 is connected to the others by being 3 away from 0,0,0,3, which is already in the constellation.
The bottom two points, 9,0,0,0 and 12,0,0,0 are in a separate constellation because no point is close enough
to connect them to the first constellation.
So, in the above list, the number of constellations is 2.
(If a point at 6,0,0,0 were present, it would connect 3,0,0,0 and 9,0,0,0,
merging all of the points into a single giant constellation instead.)

In this example, the number of constellations is 4:

-1,2,2,0
0,0,2,-2
0,0,0,-2
-1,2,0,0
-2,-2,-2,2
3,0,2,-1
-1,3,2,2
-1,0,-1,0
0,2,1,-2
3,0,0,0

In this one, it's 3:

1,-1,0,1
2,0,-1,0
3,2,-1,0
0,0,3,1
0,0,-1,-1
2,3,-2,0
-2,2,0,0
2,-2,0,-1
1,-1,0,-1
3,2,0,2

Finally, in this one, it's 8:

1,-1,-1,-2
-2,-2,0,1
0,2,1,3
-2,3,-2,1
0,2,3,-2
-1,-1,1,-2
0,-2,-1,0
-2,2,3,-1
1,2,2,0
-1,-2,0,-2

The portly man nervously strokes his white beard. It's time to get that hot chocolate.

How many constellations are formed by the fixed points in spacetime?

--- Part Two ---

A small glowing portal opens above the mug you prepared and just enough hot chocolate streams in to fill it.
You suspect the reindeer has never encountered hot chocolate before, but seems to enjoy it anyway.
You hope it works.

It's time to start worrying about that integer underflow in time itself you set up a few days ago.
You check the status of the device: "Insufficient chronal energy for activation. Energy required: 50 stars."

The reindeer bumps the device with its nose.

"Energy required: 49 stars."

You don't have enough stars to trigger the underflow, though. You need 1 more.

*/

fun manhattanDistance(from: List<Int>, to: List<Int>): Int = from.zip(to).map { (from, to) -> abs(to - from) }.sum()

fun parseSpaceTimePoint(input: String): List<Int> = input.split(",")
        .map { it.trim().toInt() }.toList()

fun parseSpaceTimePoints(input: String): List<List<Int>> = input.split("\n")
        .map { parseSpaceTimePoint(it) }

fun constellations(input: List<List<Int>>): Set<Set<List<Int>>> {

    fun fillConstellation(result: MutableSet<List<Int>>, newPoints: Set<List<Int>>, unprocessedInput: MutableList<List<Int>>) {
        val nearPoints = unprocessedInput.filter { unprocessedPoint ->
            newPoints.any { manhattanDistance(it,unprocessedPoint) <= 3}
        }
        if (nearPoints.isNotEmpty()) {
            result.addAll(nearPoints)
            unprocessedInput.removeAll(nearPoints)
            fillConstellation(result, nearPoints.toSet(), unprocessedInput)
        }
    }

    fun createConstellation(startWith: List<Int>, unprocessedInput: MutableList<List<Int>>): Set<List<Int>> {
        val result = mutableSetOf(startWith)
        fillConstellation(result, result, unprocessedInput)
        return result
    }

    val unprocessedInput = input.toMutableList()
    val result = mutableSetOf<Set<List<Int>>>()
    while(true) {
        val spaceTimePoint = unprocessedInput.firstOrNull() ?: break
        unprocessedInput.remove(spaceTimePoint)
        result.add(createConstellation(spaceTimePoint, unprocessedInput))
    }
    return result
}

class Day25Spec : Spek({

    describe("part 1") {
        describe("manhattan distance 4d") {
            val testData = arrayOf(
                    data(listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), 0),
                    data(listOf(0, 0, 0, 0), listOf(1, -1, 0, 0), 2),
                    data(listOf(0, 0, 0, 0), listOf(1, 2, 3, 4), 10),
                    data(listOf(1, 2, 3, 4), listOf(0, 0, 0, 0), 10)
            )
            onData("from %s to %s", with = *testData) { from, to, expected ->
                manhattanDistance(from, to) `should equal` expected
            }
        }
        describe("parse space time points") {
            given("a line of space time points") {
                val input = "1, 2,3, 4"
                it("should be parsed correctly") {
                    parseSpaceTimePoint(input) `should equal` listOf(1, 2, 3, 4)
                }
            }
            given("some space time points") {
                val input = """
                        1, 2, 3, 4
                        4, 3, 2, 1
                    """.trimIndent()
                it("should be parsed to a list of space time points") {
                    val result = parseSpaceTimePoints(input)
                    result `should equal` listOf(
                            listOf(1, 2, 3, 4),
                            listOf(4, 3, 2, 1)
                    )

                }
            }
        }
        describe("find constellation") {
            given("unconnected space time points") {
                val spaceTimePoints = listOf(
                        listOf(0, 0, 0, 0),
                        listOf(0, 4, 0, 0),
                        listOf(0, 0, 4, 0),
                        listOf(0, 0, 0, 4)
                )
                it("should return constellations with single space time points") {
                    constellations(spaceTimePoints) `should equal`
                            setOf(
                                    setOf(listOf(0, 0, 0, 0)),
                                    setOf(listOf(0, 4, 0, 0)),
                                    setOf(listOf(0, 0, 4, 0)),
                                    setOf(listOf(0, 0, 0, 4))
                            )
                }
            }
            given("connected space time points") {
                val spaceTimePoints = listOf(
                        listOf(0, 0, 0, 0),
                        listOf(0, 3, 0, 0)
                )
                it("should return constellations with constellation") {
                    constellations(spaceTimePoints) `should equal`
                            setOf(
                                    setOf(listOf(0, 0, 0, 0), listOf(0, 3, 0, 0))
                            )
                }
            }
            given("more connected space time points") {
                val spaceTimePoints = listOf(
                        listOf(0, 0, 0, 0),
                        listOf(0, 3, 0, 0),
                        listOf(0, 3, 3, 0)

                )
                it("should return constellations with constellation") {
                    constellations(spaceTimePoints) `should equal`
                            setOf(
                                    setOf(listOf(0, 0, 0, 0), listOf(0, 3, 0, 0), listOf(0, 3, 3, 0))
                            )
                }
            }
        }
        describe("examples") {
            describe("find constellations for examples") {
                val testData = arrayOf(
                        data(
                                """
                         0,0,0,0
                         3,0,0,0
                         0,3,0,0
                         0,0,3,0
                         0,0,0,3
                         0,0,0,6
                         9,0,0,0
                        12,0,0,0 
                        """.trimIndent(), 2),
                        data(
                                """
                        -1,2,2,0
                        0,0,2,-2
                        0,0,0,-2
                        -1,2,0,0
                        -2,-2,-2,2
                        3,0,2,-1
                        -1,3,2,2
                        -1,0,-1,0
                        0,2,1,-2
                        3,0,0,0
                        """.trimIndent(), 4),
                        data(
                                """
                        1,-1,0,1
                        2,0,-1,0
                        3,2,-1,0
                        0,0,3,1
                        0,0,-1,-1
                        2,3,-2,0
                        -2,2,0,0
                        2,-2,0,-1
                        1,-1,0,-1
                        3,2,0,2
                        """.trimIndent(), 3),
                        data(
                                """
                        1,-1,-1,-2
                        -2,-2,0,1
                        0,2,1,3
                        -2,3,-2,1
                        0,2,3,-2
                        -1,-1,1,-2
                        0,-2,-1,0
                        -2,2,3,-1
                        1,2,2,0
                        -1,-2,0,-2
                        """.trimIndent(), 8)
                )

                onData("input %s", with = *testData) { inputString, expected ->
                    val input = parseSpaceTimePoints(inputString)
                    constellations(input).size `should equal` expected
                }
            }
        }
        describe("exercise") {
            given("exercise input") {
                val inputString = readResource("day25Input.txt")
                val input = parseSpaceTimePoints(inputString)
                it("should find the right number of constellations") {
                    constellations(input).size `should equal` 346               }
            }
        }
    }
})
