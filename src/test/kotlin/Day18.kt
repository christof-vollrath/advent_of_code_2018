import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.dsl.on

/*
--- Day 18: Settlers of The North Pole ---

On the outskirts of the North Pole base construction project, many Elves are collecting lumber.

The lumber collection area is 50 acres by 50 acres; each acre can be either open ground (.), trees (|),
or a lumberyard (#).
You take a scan of the area (your puzzle input).

Strange magic is at work here: each minute, the landscape looks entirely different.
In exactly one minute, an open acre can fill with trees, a wooded acre can be converted to a lumberyard,
or a lumberyard can be cleared to open ground (the lumber having been sent to other projects).

The change to each acre is based entirely on the contents of that acre as well as the number of open, wooded,
or lumberyard acres adjacent to it at the start of each minute.
Here, "adjacent" means any of the eight acres surrounding that acre.
(Acres on the edges of the lumber collection area might have fewer than eight adjacent acres;
the missing acres aren't counted.)

In particular:

An open acre will become filled with trees if three or more adjacent acres contained trees. Otherwise, nothing happens.
An acre filled with trees will become a lumberyard if three or more adjacent acres were lumberyards.
Otherwise, nothing happens.
An acre containing a lumberyard will remain a lumberyard if it was adjacent to at least one other lumberyard
and at least one acre containing trees. Otherwise, it becomes open.
These changes happen across all acres simultaneously, each of them using the state of all acres at the beginning
of the minute and changing to their new form by the end of that same minute.
Changes that happen during the minute don't affect each other.

For example, suppose the lumber collection area is instead only 10 by 10 acres with this initial configuration:

Initial state:
.#.#...|#.
.....#|##|
.|..|...#.
..|#.....#
#.#|||#|#|
...#.||...
.|....|...
||...#|.#|
|.||||..|.
...#.|..|.

After 1 minute:
.......##.
......|###
.|..|...#.
..|#||...#
..##||.|#|
...#||||..
||...|||..
|||||.||.|
||||||||||
....||..|.

After 2 minutes:
.......#..
......|#..
.|.|||....
..##|||..#
..###|||#|
...#|||||.
|||||||||.
||||||||||
||||||||||
.|||||||||

After 3 minutes:
.......#..
....|||#..
.|.||||...
..###|||.#
...##|||#|
.||##|||||
||||||||||
||||||||||
||||||||||
||||||||||

After 4 minutes:
.....|.#..
...||||#..
.|.#||||..
..###||||#
...###||#|
|||##|||||
||||||||||
||||||||||
||||||||||
||||||||||

After 5 minutes:
....|||#..
...||||#..
.|.##||||.
..####|||#
.|.###||#|
|||###||||
||||||||||
||||||||||
||||||||||
||||||||||

After 6 minutes:
...||||#..
...||||#..
.|.###|||.
..#.##|||#
|||#.##|#|
|||###||||
||||#|||||
||||||||||
||||||||||
||||||||||

After 7 minutes:
...||||#..
..||#|##..
.|.####||.
||#..##||#
||##.##|#|
|||####|||
|||###||||
||||||||||
||||||||||
||||||||||

After 8 minutes:
..||||##..
..|#####..
|||#####|.
||#...##|#
||##..###|
||##.###||
|||####|||
||||#|||||
||||||||||
||||||||||

After 9 minutes:
..||###...
.||#####..
||##...##.
||#....###
|##....##|
||##..###|
||######||
|||###||||
||||||||||
||||||||||

After 10 minutes:
.||##.....
||###.....
||##......
|##.....##
|##.....##
|##....##|
||##.####|
||#####|||
||||#|||||
||||||||||

After 10 minutes, there are 37 wooded acres and 31 lumberyards.
Multiplying the number of wooded acres by the number of lumberyards gives the total resource value after ten minutes:
37 * 31 = 1147.

What will the total resource value of the lumber collection area be after 10 minutes?

--- Part Two ---

This important natural resource will need to last for at least thousands of years.
Are the Elves collecting this lumber sustainably?

What will the total resource value of the lumber collection area be after 1000000000 minutes?


 */

fun parseLumberArea(input: String): LumberArea {
    val areaTypes = input.split("\n")
            .map { line ->
                line.map {
                    areaTypeValueOf(it)
                }.toTypedArray()
            }.toTypedArray()
    return LumberArea(areaTypes)
}

fun areaTypeValueOf(c: Char) = enumValues<AreaType>().first { it.c == c }

enum class AreaType(val c: Char) {
    OPEN('.'),
    TREES('|'),
    LUMBER('#');

    override fun toString() = c.toString()
    fun executeMinute(adjacentAreaTypeCounts: Map<AreaType, Int>) =
            when(this) {
                OPEN ->
                    if ((adjacentAreaTypeCounts[TREES] ?: 0) >= 3) TREES
                    else OPEN
                TREES ->
                    if ((adjacentAreaTypeCounts[LUMBER] ?: 0) >= 3) LUMBER
                    else TREES
                LUMBER ->
                    if ((adjacentAreaTypeCounts[LUMBER] ?: 0) >= 1 && (adjacentAreaTypeCounts[TREES] ?: 0) >= 1) LUMBER
                    else OPEN
            }
}

class LumberArea(val grid: Array<Array<AreaType>>) {
    override fun toString() =
            grid.joinToString("") { row ->
                row.joinToString("", postfix = "\n") { element -> element.toString() }
            }

    fun get(x: Int, y: Int) = grid[y][x]
    fun executeMinute(): LumberArea {
        val nextGrid = grid.mapIndexed { y , row ->
            row.mapIndexed { x, area ->
                val adjacentAreaTypeCounts = countAdjacentAreaTypes(x, y)
                area.executeMinute(adjacentAreaTypeCounts)
            }.toTypedArray()
        }.toTypedArray()
        return LumberArea(nextGrid)
    }

    fun countAdjacentAreaTypes(x: Int, y: Int): Map<AreaType, Int> =
            countAreaTypes(adjacentAreas(x, y))

    fun countAreaTypes(areaTypes: List<AreaType>) = areaTypes.groupBy { it }.map { entry -> entry.key to entry.value.size }.toMap()

    fun countAreaTypes() = with(grid.flatMap { row -> row.map { it } }) {
        countAreaTypes(this)
    }

    fun adjacentAreas(x: Int, y: Int): List<AreaType> =
            (-1..1).flatMap { dy ->
                (-1..1).mapNotNull { dx ->
                    val x2 = x + dx
                    val y2 = y + dy
                    when {
                        dx == 0 && dy == 0 -> null
                        x2 < 0 -> null
                        y2 < 0 -> null
                        y2 >= grid.size -> null
                        x2 >= grid[y2].size -> null
                        else -> get(x2, y2)
                    }
                }
            }

    fun executeMinutes(n: Int): LumberArea = if (n == 0) this
    else executeMinute().executeMinutes(n - 1)

    fun totalResources() = with(countAreaTypes()) { (this[AreaType.TREES] ?: 0) * (this[AreaType.LUMBER] ?: 0) }

}

class Day18Spec : Spek({

    describe("part 1") {
        given("input lamber area") {
            val inputString = """
                    .#.#...|#.
                    .....#|##|
                    .|..|...#.
                    ..|#.....#
                    #.#|||#|#|
                    ...#.||...
                    .|....|...
                    ||...#|.#|
                    |.||||..|.
                    ...#.|..|.
                """.trimIndent()
            val input = parseLumberArea(inputString)
            describe("parse and print") {
                it("should be parsed and printed again") {
                    input.toString() `should equal` inputString + "\n"
                }
            }
            describe("1 minute") {
                it("should execute action of one minute") {
                    val result = input.executeMinute()
                    result.toString() `should equal` """
                            .......##.
                            ......|###
                            .|..|...#.
                            ..|#||...#
                            ..##||.|#|
                            ...#||||..
                            ||...|||..
                            |||||.||.|
                            ||||||||||
                            ....||..|.
                            
                            """.trimIndent()
                }
            }
            describe("10 minutes") {
                on("execute action of ten minutes") {
                    val result = input.executeMinutes(10)
                    it("should execute action of ten minutes") {
                        result.toString() `should equal` """
                            .||##.....
                            ||###.....
                            ||##......
                            |##.....##
                            |##.....##
                            |##....##|
                            ||##.####|
                            ||#####|||
                            ||||#|||||
                            ||||||||||
                            
                            """.trimIndent()
                    }
                    it("should count areas") {
                        val counts = result.countAreaTypes()
                        counts[AreaType.TREES] `should equal` 37
                        counts[AreaType.LUMBER] `should equal` 31
                    }
                    it("should calculate total resources") {
                        result.totalResources() `should equal` 1147
                    }

                }
            }
            describe("count adjacent areas") {
                it("should get adjacent areas") {
                    input.adjacentAreas(6, 1) `should equal` listOf(
                            AreaType.OPEN, AreaType.OPEN, AreaType.TREES,
                            AreaType.LUMBER, AreaType.LUMBER,
                            AreaType.OPEN, AreaType.OPEN, AreaType.OPEN
                            )
                }
                it("should count adjacent areas for an area in the middle") {
                    input.countAdjacentAreaTypes(6, 1) `should equal` mapOf(
                            AreaType.OPEN to 5,
                            AreaType.TREES to 1,
                            AreaType.LUMBER to 2
                    )
                }
                it("should count adjacent areas at the upper left boarder") {
                    input.countAdjacentAreaTypes(9, 9) `should equal` mapOf(
                            AreaType.OPEN to 1,
                            AreaType.TREES to 2
                    )
                    it("should count adjacent areas at the lower right boarder") {
                        input.countAdjacentAreaTypes(0, 0) `should equal` mapOf(
                                AreaType.OPEN to 2,
                                AreaType.LUMBER to 1
                        )
                    }
                }
            }
        }
        given("exercise input") {
            val inputString = readResource("day18Input.txt")
            val input = parseLumberArea(inputString)
            val result = input.executeMinutes(10)
            println(result)
            it("should calculate total resources") {
                result.totalResources() `should equal` 360720
            }
        }
    }
})
