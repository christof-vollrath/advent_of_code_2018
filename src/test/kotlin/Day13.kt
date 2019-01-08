import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.lang.IllegalArgumentException
import javax.sound.midi.Track

/*
--- Day 13: Mine Cart Madness ---

A crop of this size requires significant logistics to transport produce, soil, fertilizer, and so on.
The Elves are very busy pushing things around in carts on some kind of rudimentary system of tracks they've come up with.

Seeing as how cart-and-track systems don't appear in recorded history for another 1000 years,
the Elves seem to be making this up as they go along.
They haven't even figured out how to avoid collisions yet.

You map out the tracks (your puzzle input) and see where you can help.

Tracks consist of straight paths (| and -), curves (/ and \), and intersections (+).
Curves connect exactly two perpendicular pieces of track; for example, this is a closed loop:

/----\
|    |
|    |
\----/

Intersections occur when two perpendicular paths cross.
At an intersection, a cart is capable of turning left, turning right, or continuing straight.
Here are two loops connected by two intersections:

/-----\
|     |
|  /--+--\
|  |  |  |
\--+--/  |
   |     |
   \-----/

Several carts are also on the tracks.
Carts always face either up (^), down (v), left (<), or right (>).
(On your initial map, the track under each cart is a straight path matching the direction the cart is facing.)

Each time a cart has the option to turn (by arriving at any intersection),
it turns left the first time, goes straight the second time, turns right the third time,
and then repeats those directions starting again with left the fourth time,
straight the fifth time, and so on.
This process is independent of the particular intersection at which the cart has arrived - that is,
the cart has no per-intersection memory.

Carts all move at the same speed; they take turns moving a single step at a time.
They do this based on their current location: carts on the top row move first (acting from left to right),
then carts on the second row move (again from left to right), then carts on the third row, and so on.
Once each cart has moved one step, the process repeats; each of these loops is called a tick.

For example, suppose there are two carts on a straight track:

|  |  |  |  |
v  |  |  |  |
|  v  v  |  |
|  |  |  v  X
|  |  ^  ^  |
^  ^  |  |  |
|  |  |  |  |

First, the top cart moves. It is facing down (v), so it moves down one square.
Second, the bottom cart moves. It is facing up (^), so it moves up one square.
Because all carts have moved, the first tick ends. Then, the process repeats, starting with the first cart.
The first cart moves down, then the second cart moves up - right into the first cart, colliding with it!
(The location of the crash is marked with an X.) This ends the second and last tick.

Here is a longer example:

/->-\
|   |  /----\
| /-+--+-\  |
| | |  | v  |
\-+-/  \-+--/
  \------/

/-->\
|   |  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \->--/
  \------/

/---v
|   |  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-+>-/
  \------/

/---\
|   v  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-+->/
  \------/

/---\
|   |  /----\
| /->--+-\  |
| | |  | |  |
\-+-/  \-+--^
  \------/

/---\
|   |  /----\
| /-+>-+-\  |
| | |  | |  ^
\-+-/  \-+--/
  \------/

/---\
|   |  /----\
| /-+->+-\  ^
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /----<
| /-+-->-\  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /---<\
| /-+--+>\  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /--<-\
| /-+--+-v  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /-<--\
| /-+--+-\  |
| | |  | v  |
\-+-/  \-+--/
  \------/

/---\
|   |  /<---\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-<--/
  \------/

/---\
|   |  v----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \<+--/
  \------/

/---\
|   |  /----\
| /-+--v-\  |
| | |  | |  |
\-+-/  ^-+--/
  \------/

/---\
|   |  /----\
| /-+--+-\  |
| | |  X |  |
\-+-/  \-+--/
  \------/

After following their respective paths for a while, the carts eventually crash.
To help prevent crashes, you'd like to know the location of the first crash.
Locations are given in X,Y coordinates, where the furthest left column is X=0 and the furthest top row is Y=0:

           111
 0123456789012
0/---\
1|   |  /----\
2| /-+--+-\  |
3| | |  X |  |
4\-+-/  \-+--/
5  \------/

In this example, the location of the first crash is 7,3.

 */

class Day13Spec : Spek({

    describe("part 1") {
        describe("parse rail map") {
            given("simple example without intersections") {
                val mapString = """
                    /-\
                    | |
                    \-/
                """.trimIndent()

                it("should be parsed correctly") {
                    val railMap = parseRailMap(mapString)
                    railMap.size `should equal` 8
                    railMap `should equal` mapOf(
                            Pair(0, 0) to TrackElement.UpperRightCurve,
                            Pair(1, 0) to TrackElement.HorizontalStraight,
                            Pair(2, 0) to TrackElement.UpperLeftCurve,
                            Pair(0, 1) to TrackElement.VerticalStraight,
                            Pair(2, 1) to TrackElement.VerticalStraight,
                            Pair(0, 2) to TrackElement.LowerRightCurve,
                            Pair(1, 2) to TrackElement.HorizontalStraight,
                            Pair(2, 2) to TrackElement.LowerLeftCurve
                    )
                    printRailMap(railMap) `should equal` mapString
                }
            }
            given("simple example with intersections") {
                val mapString = """
                    /-\
                    |/+\
                    \+/|
                     \-/
                """.trimIndent()

                it("should be parsed correctly") {
                    val railMap = parseRailMap(mapString)
                    railMap.size `should equal` 14
                    railMap `should equal` mapOf(
                            Pair(0, 0) to TrackElement.UpperRightCurve,
                            Pair(1, 0) to TrackElement.HorizontalStraight,
                            Pair(2, 0) to TrackElement.UpperLeftCurve,
                            Pair(0, 1) to TrackElement.VerticalStraight,
                            Pair(1, 1) to TrackElement.UpperRightCurve,
                            Pair(2, 1) to TrackElement.Intersection,
                            Pair(3, 1) to TrackElement.UpperLeftCurve,
                            Pair(0, 2) to TrackElement.LowerRightCurve,
                            Pair(1, 2) to TrackElement.Intersection,
                            Pair(2, 2) to TrackElement.LowerLeftCurve,
                            Pair(3, 2) to TrackElement.VerticalStraight,
                            Pair(1, 3) to TrackElement.LowerRightCurve,
                            Pair(2, 3) to TrackElement.HorizontalStraight,
                            Pair(3, 3) to TrackElement.LowerLeftCurve
                    )
                    printRailMap(railMap) `should equal` mapString
                }
            }
            given("simple example with intersections and carts") {
                val mapString = """
                    />\
                    ^/+\
                    \+/v
                     \</
                """.trimIndent()

                it("should be parsed correctly") {
                    val (railMap, carts) = parseRailMapWithCarts(mapString)
                    railMap.size `should equal` 14
                    railMap `should equal` mapOf(
                            Pair(0, 0) to TrackElement.UpperRightCurve,
                            Pair(1, 0) to TrackElement.HorizontalStraight,
                            Pair(2, 0) to TrackElement.UpperLeftCurve,
                            Pair(0, 1) to TrackElement.VerticalStraight,
                            Pair(1, 1) to TrackElement.UpperRightCurve,
                            Pair(2, 1) to TrackElement.Intersection,
                            Pair(3, 1) to TrackElement.UpperLeftCurve,
                            Pair(0, 2) to TrackElement.LowerRightCurve,
                            Pair(1, 2) to TrackElement.Intersection,
                            Pair(2, 2) to TrackElement.LowerLeftCurve,
                            Pair(3, 2) to TrackElement.VerticalStraight,
                            Pair(1, 3) to TrackElement.LowerRightCurve,
                            Pair(2, 3) to TrackElement.HorizontalStraight,
                            Pair(3, 3) to TrackElement.LowerLeftCurve
                    )
                    carts `should equal` setOf(
                            Cart(Pair(1, 0), Direction.Right),
                            Cart(Pair(0, 1), Direction.Up),
                            Cart(Pair(3, 2), Direction.Down),
                            Cart(Pair(2, 3), Direction.Left)
                    )
                    printRailMap(railMap, carts) `should equal` mapString
                }
            }
        }
    }
})

enum class TrackElement {
    UpperRightCurve,
    UpperLeftCurve,
    HorizontalStraight,
    VerticalStraight,
    LowerRightCurve,
    LowerLeftCurve,
    Intersection
}

enum class Direction { Up, Down, Left, Right }

data class Cart(val position: Pair<Int, Int>, val direction: Direction)

fun printRailMap(railMap: Map<Pair<Int, Int>, TrackElement>, carts: Set<Cart> = emptySet()): String {
    val cartsMap = carts.map { it.position to it }.toMap()
    val maxX = railMap.keys.maxBy { it.first}!!.first
    val maxY = railMap.keys.maxBy { it.second}!!.second
    return (0 .. maxY).map { y ->
        (0 .. maxX).map {x ->
            when(cartsMap[Pair(x, y)]?.direction) {
                Direction.Up -> '^'
                Direction.Down -> 'v'
                Direction.Left -> '<'
                Direction.Right -> '>'
                else ->
                    when(railMap[Pair(x, y)]) {
                        null -> ' '
                        TrackElement.VerticalStraight -> '|'
                        TrackElement.HorizontalStraight -> '-'
                        TrackElement.Intersection -> '+'
                        TrackElement.UpperRightCurve, TrackElement.LowerLeftCurve -> '/'
                        TrackElement.UpperLeftCurve, TrackElement.LowerRightCurve -> '\\'
                    }
            }
        }.joinToString("").trimEnd()
    }.joinToString("\n")

}

fun parseRailMap(mapString: String): Map<Pair<Int, Int>, TrackElement> =
    mapString.split("\n").mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val trackElement = when(c) {
                '-', '>', '<' -> TrackElement.HorizontalStraight
                '|', '^', 'v' -> TrackElement.VerticalStraight
                '+' -> TrackElement.Intersection
                '/' -> {
                    val left = line.getOrNull(x - 1)
                    val right = line.getOrNull(x + 1)
                    when {
                        isVerticalOrEquivalent(right) -> TrackElement.UpperRightCurve
                        isVerticalOrEquivalent(left) -> TrackElement.LowerLeftCurve
                        else -> throw IllegalArgumentException("Illegal curve at x=$x y=$y $c")
                    }
                }
                '\\' -> {
                    val left = line.getOrNull(x - 1)
                    val right = line.getOrNull(x + 1)
                    when {
                        isVerticalOrEquivalent(right) -> TrackElement.LowerRightCurve
                        isVerticalOrEquivalent(left) -> TrackElement.UpperLeftCurve
                        else -> throw IllegalArgumentException("Illegal curve at x=$x y=$y $c")
                    }
                }
                ' ' -> null
                else -> throw IllegalArgumentException("Illegal track element $c")
            }
            if (trackElement == null) null
            else Pair(x, y) to trackElement
        }.filterNotNull()
    }.flatten().toMap()

fun isVerticalOrEquivalent(left: Char?) = left == '-' || left == '+' || left == '>' || left == '<'

fun parseRailMapWithCarts(mapString: String): Pair<Map<Pair<Int, Int>, TrackElement>, Set<Cart>> {
    val railMap = parseRailMap(mapString)
    val carts = mapString.split("\n").mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val direction = when(c) {
                '^' -> Direction.Up
                'v' -> Direction.Down
                '<' -> Direction.Left
                '>' -> Direction.Right
                else -> null
            }
            if (direction != null) Cart(Pair(x, y), direction)
            else null
        }.filterNotNull()
    }.flatten().toSet()
    return Pair(railMap, carts)
}
