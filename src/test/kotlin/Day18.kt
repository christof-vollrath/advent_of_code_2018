import org.jetbrains.spek.api.Spek
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.dsl.*

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

    fun executeMinutes(n: Int) = executeMinutes(n, this)

    tailrec fun executeMinutes(n: Int, currentArea: LumberArea): LumberArea {
        if (n % 1000 == 0) { println(n); println(currentArea) }
        return if (n == 0) currentArea
        else {
            val nextArea = currentArea.executeMinute()
            if (nextArea == currentArea) nextArea // Stop when nothing changes anymore
            else executeMinutes(n - 1, currentArea.executeMinute())
        }
    }

    fun totalResources() = with(countAreaTypes()) { (this[AreaType.TREES] ?: 0) * (this[AreaType.LUMBER] ?: 0) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LumberArea
        if (!grid.contentDeepEquals(other.grid)) return false
        return true
    }

    override fun hashCode(): Int {
        return grid.contentDeepHashCode()
    }
}

fun <T> repeatWithLoopDetection(n: Int, start: T, function: (T) -> T): T {
    val cache = mutableMapOf<T, Int>()
    var currentValue = start
    var i = 0
    var loopFound = false
    while (i < n) {
        currentValue = function(currentValue)
        val cachedValue = cache[currentValue]
        if (!loopFound && cachedValue != null) {
            println("loop detected from $cachedValue to $i")
            val loopSize = i - cachedValue
            val skipTo = (n - cachedValue) / loopSize * loopSize + cachedValue
            println("skip to $skipTo with value \n$currentValue ---\n")
            loopFound = true
            i = skipTo + 1
        } else {
            cache[currentValue] = i
            i++
        }
    }
    return currentValue
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
            it("should calculate total resources for 10 minutes") {
                val result = input.executeMinutes(10)
                println(result)
                result.totalResources() `should equal` 360720
            }
        }
    }
    describe("part 2") {
        describe("repeat with loop detection") {
            given("a test data generator") {
                fun generateTestData(i: Int) =
                        if (i <= 3) i
                        else (i - 4) % 4 + 4
                it("should generate data") {
                    (1..21).map { generateTestData(it) } `should equal` listOf(1,2,3,4,5,6,7,4,5,6,7,4,5,6,7,4,5,6,7,4,5) // without loop detection
                }
                it("should repeat skiping the loop") {
                    repeatWithLoopDetection(20, 1) { i -> generateTestData(i + 1) } `should equal` 5
                }
            }
        }
        given("exercise input") {
            val inputString = readResource("day18Input.txt")
            val input = parseLumberArea(inputString)
            it("should calculate total resources for 10 minutes with loop detection, even when no loops are detected") {
                val result = repeatWithLoopDetection(10, input) {currentArea -> currentArea.executeMinute() }
                println(result)
                result.totalResources() `should equal` 360720
            }
            it("should calculate total resources for 1000000000 minutes") {
                val result = repeatWithLoopDetection(1000000000, input) { currentArea -> currentArea.executeMinute() }
                println(result)
                result.totalResources() `should equal` 197276
            }
        }
    }
})
