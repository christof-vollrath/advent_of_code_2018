import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 3: No Matter How You Slice It ---
The Elves managed to locate the chimney-squeeze prototype fabric for Santa's suit
(thanks to someone who helpfully wrote its box IDs on the wall of the warehouse in the middle of the night).
Unfortunately, anomalies are still affecting them - nobody can even agree on how to cut the fabric.

The whole piece of fabric they're working on is a very large square - at least 1000 inches on each side.

Each Elf has made a claim about which area of fabric would be ideal for Santa's suit.
All claims have an ID and consist of a single rectangle with edges parallel to the edges of the fabric.
Each claim's rectangle is defined as follows:

The number of inches between the left edge of the fabric and the left edge of the rectangle.
The number of inches between the top edge of the fabric and the top edge of the rectangle.
The width of the rectangle in inches.
The height of the rectangle in inches.

A claim like #123 @ 3,2: 5x4 means that claim ID 123 specifies a rectangle 3 inches from the left edge,
2 inches from the top edge, 5 inches wide, and 4 inches tall.
Visually, it claims the square inches of fabric represented by #
(and ignores the square inches of fabric represented by .) in the diagram below:

...........
...........
...#####...
...#####...
...#####...
...#####...
...........
...........
...........

The problem is that many of the claims overlap, causing two or more claims to cover part of the same areas.
For example, consider the following claims:

#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2

Visually, these claim the following areas:

........
...2222.
...2222.
.11XX22.
.11XX22.
.111133.
.111133.
........

The four square inches marked with X are claimed by both 1 and 2.
(Claim 3, while adjacent to the others, does not overlap either of them.)

If the Elves all proceed with their own plans, none of them will have enough fabric.
How many square inches of fabric are within two or more claims?

--- Part Two ---

Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square inch
of fabric with any other claim.
If you can somehow draw attention to it, maybe the Elves will be able to make Santa's suit after all!

For example, in the claims above, only claim 3 is intact after all claims are made.

What is the ID of the only claim that doesn't overlap?


 */

class Day03Spec : Spek({

    describe("part 1") {
        given("one claim") {
            val input = "#1 @ 3,2: 5x4"

            it("should be parsed") {
                parseClaim(input) `should equal` Claim(id = 1, x = 3, y = 2, width = 5, height = 4)
            }

            it("should be printed") {
                parseClaim(input).toStringMap() `should equal` """
                    ........
                    ........
                    ...11111
                    ...11111
                    ...11111
                    ...11111
                """.trimIndent()
            }
        }
        given("some claims") {
            val claim1 = parseClaim("#1 @ 1,3: 4x4")
            val claim2 = parseClaim("#2 @ 3,1: 4x4")
            val claim3 = parseClaim("#3 @ 5,5: 2x2")
            val mergedClaim = claim1 + claim2 + claim3

            it("should be merged to one claim") {
                mergedClaim.toStringMap() `should equal` """
                    .......
                    ...2222
                    ...2222
                    .11XX22
                    .11XX22
                    .111133
                    .111133
                """.trimIndent()
            }
            it("should have correct count of two or more claims") {
                mergedClaim.countTwoOrMoreClaims() `should equal` 4
            }

        }
        given("list of claims") {
            val claims = listOf("#1 @ 1,3: 4x4", "#2 @ 3,1: 4x4", "#3 @ 5,5: 2x2").map { parseClaim(it) }

            val mergedClaim = mergeClaims(claims)
            it("should have correct count for list of claims") {
                mergedClaim.countTwoOrMoreClaims() `should equal` 4
            }

        }
        given("exercise") {
            val input = readResource("day03Input.txt").split('\n').map { parseClaim(it) }
            it("should calculate correct result") {
                val result = mergeClaims(input)
                result.countTwoOrMoreClaims() `should equal` 101469
            }

        }

    }
})

fun mergeClaims(claims: List<Claim>) = claims.drop(1).fold(claims.first()) { acc: AbstractClaim, claim ->  acc + claim }

operator fun AbstractClaim.plus(other: AbstractClaim) = MergedClaim(charMap).also {
    other.charMap.forEach { pos, c ->
        val current = it.charMap[pos]
        if (current == null || current == '.') it.charMap[pos] = c
        else it.charMap[pos] = 'X'
    }
}

abstract class AbstractClaim() {
    abstract val charMap: MutableMap<Pair<Int, Int>, Char>

    fun toStringMap(): String {
        val maxX = charMap.keys.maxBy { it.first }!!.first
        val maxY = charMap.keys.maxBy { it.second }!!.second
        val chars = (1..maxY).map { y ->
            (1..maxX).map { x ->
                charMap[x to y] ?: '.'
            }
        }
        return chars.map { it.joinToString("")}.joinToString("\n")
    }

    fun countTwoOrMoreClaims() = charMap.values.filter { it == 'X'}.count()
}

data class Claim(val id: Int, val x: Int, val y: Int, val width: Int, val height: Int) : AbstractClaim() {
    override val charMap =
        mutableMapOf<Pair<Int, Int>, Char>().also {
            (1..width).forEach { dx ->
                (1..height).forEach { dy ->
                    it[(x + dx) to (y + dy)] = (id % 10).toString()[0]
                }
            }
        }
}

class MergedClaim(override val charMap: MutableMap<Pair<Int, Int>, Char>) : AbstractClaim()

fun parseClaim(input: String): Claim {
    val  regex = """#(\d+)\s*@\s*(\d+)\s*,\s*(\d+)\s*:\s*(\d+)\s*x\s*(\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input")
    if (match.groupValues.size != 6) throw IllegalArgumentException("Not all elements parsed")
    val values = match.groupValues
    return Claim(values[1].toInt(), values[2].toInt(), values[3].toInt(), values[4].toInt(), values[5].toInt())
}
