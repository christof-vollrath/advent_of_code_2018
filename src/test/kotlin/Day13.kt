import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
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
            given("example") {
                val mapString = """
                    /->-\
                    |   |  /----\
                    | /-+--+-\  |
                    | | |  | v  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent()
                val steps = listOf(
                    """
                    /-->\
                    |   |  /----\
                    | /-+--+-\  |
                    | | |  | |  |
                    \-+-/  \->--/
                      \------/
                    """.trimIndent(),
                    """
                    /---v
                    |   |  /----\
                    | /-+--+-\  |
                    | | |  | |  |
                    \-+-/  \-+>-/
                      \------/
                    """.trimIndent(),
                        """
                    /---\
                    |   v  /----\
                    | /-+--+-\  |
                    | | |  | |  |
                    \-+-/  \-+->/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /----\
                    | /->--+-\  |
                    | | |  | |  |
                    \-+-/  \-+--^
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /----\
                    | /-+>-+-\  |
                    | | |  | |  ^
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /----\
                    | /-+->+-\  ^
                    | | |  | |  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /----<
                    | /-+-->-\  |
                    | | |  | |  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /---<\
                    | /-+--+>\  |
                    | | |  | |  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /--<-\
                    | /-+--+-v  |
                    | | |  | |  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /-<--\
                    | /-+--+-\  |
                    | | |  | v  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /<---\
                    | /-+--+-\  |
                    | | |  | |  |
                    \-+-/  \-<--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  v----\
                    | /-+--+-\  |
                    | | |  | |  |
                    \-+-/  \<+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /----\
                    | /-+--v-\  |
                    | | |  | |  |
                    \-+-/  ^-+--/
                      \------/
                    """.trimIndent(),
                    """
                    /---\
                    |   |  /----\
                    | /-+--+-\  |
                    | | |  X |  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent()
                )
                val (railMap, carts) = parseRailMapWithCarts(mapString)
                var movedCarts = carts
                it("should be moved correctly for all steps") {
                    steps.forEachIndexed { i, step ->
                        movedCarts = moveCarts(movedCarts, railMap)
                        printRailMap(railMap, movedCarts) `should equal` step
                    }
                }
            }
        }
        given("example") {
            val mapString = """
                    /->-\
                    |   |  /----\
                    | /-+--+-\  |
                    | | |  | v  |
                    \-+-/  \-+--/
                      \------/
                    """.trimIndent()
            val (railMap, carts) = parseRailMapWithCarts(mapString)
            it("should find collision") {
                findFirstCollision(railMap, carts) `should equal` Pair(7, 3)
            }
        }
        given("exercise") {
            val mapString = readResource("day13Input.txt")
            val (railMap, carts) = parseRailMapWithCarts(mapString)
            it("should find collision") {
                findFirstCollision(railMap, carts) `should equal` Pair(7, 3)
            }
        }
    }
})

fun findFirstCollision(railMap: RailMap, carts: Set<Cart>): Pair<Int, Int> {
    var movedCarts = carts
    var i = 0
    while(true) {
        println("i=$i"); i++
        if (i in 12..15) println(printRailMap(railMap, carts))
        val collisions = findCollisions(movedCarts)
        if (collisions.isNotEmpty()) return collisions.first()
        try {
            movedCarts = moveCarts(movedCarts, railMap)
        } catch(e: Exception) {
            println(e)
            println(printRailMap(railMap, carts))
            throw e
        }
    }
}

fun moveCarts(carts: Set<Cart>, railMap: RailMap): Set<Cart> = carts.map { moveCart(it, railMap) }.toSet()

fun moveCart(cart: Cart, railMap: RailMap) = with(cart) {
    val nextPosition = when (direction) {
        Direction.Up -> Pair(position.first, position.second - 1)
        Direction.Down -> Pair(position.first, position.second + 1)
        Direction.Right -> Pair(position.first + 1, position.second)
        Direction.Left -> Pair(position.first - 1, position.second)
    }
    val (nextDirection, nextTurnState) = when(railMap[nextPosition]) {
        TrackElement.HorizontalStraight, TrackElement.VerticalStraight -> direction to turnState
        TrackElement.UpperRightCurve -> if (direction == Direction.Up) Direction.Right to turnState else Direction.Down to turnState
        TrackElement.UpperLeftCurve -> if (direction == Direction.Up) Direction.Left to turnState else Direction.Down to turnState
        TrackElement.LowerRightCurve -> if (direction == Direction.Down) Direction.Left to turnState else Direction.Up to turnState
        TrackElement.LowerLeftCurve -> if (direction == Direction.Down) Direction.Right to turnState else Direction.Up to turnState
        TrackElement.Intersection -> toggleDirection(direction, turnState)
        else -> throw IllegalStateException("Out of track $this")
    }
    Cart(nextPosition, nextDirection, nextTurnState)
}

fun toggleDirection(direction: Direction, turnState: TurnState): Pair<Direction, TurnState> {
    val nextDirection = when(turnState) {
        TurnState.Left -> when(direction) {
            Direction.Up -> Direction.Left
            Direction.Down -> Direction.Right
            Direction.Left -> Direction.Down
            Direction.Right -> Direction.Up
        }
        TurnState.Right -> when(direction) {
            Direction.Up -> Direction.Right
            Direction.Down -> Direction.Left
            Direction.Left -> Direction.Up
            Direction.Right -> Direction.Down
        }
        TurnState.Straight -> direction
    }
    val nextTurnState = when(turnState) {
        TurnState.Left -> TurnState.Straight
        TurnState.Straight -> TurnState.Right
        TurnState.Right -> TurnState.Left
    }
    return Pair(nextDirection, nextTurnState)
}

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
enum class TurnState { Left, Straight, Right }

data class Cart(val position: Pair<Int, Int>, val direction: Direction, val turnState: TurnState = TurnState.Left)

typealias RailMap = Map<Pair<Int, Int>, TrackElement>

fun printRailMap(railMap: RailMap, carts: Set<Cart> = emptySet()): String {
    val collisions = findCollisions(carts)
    val cartsMap = carts.map { it.position to it }.toMap()
    val maxX = railMap.keys.maxBy { it.first}!!.first
    val maxY = railMap.keys.maxBy { it.second}!!.second
    return (0 .. maxY).map { y ->
        (0 .. maxX).map {x ->
            if (Pair(x, y) in collisions) 'X'
            else when(cartsMap[Pair(x, y)]?.direction) {
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

fun findCollisions(carts: Set<Cart>): Set<Pair<Int, Int>> = carts.groupBy { it.position }.entries
            .filter { it.value.size > 1 }
            .map { it.key }
            .toSet()


fun parseRailMap(mapString: String): RailMap =
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

fun parseRailMapWithCarts(mapString: String): Pair<RailMap, Set<Cart>> {
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

