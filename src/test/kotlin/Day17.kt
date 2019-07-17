import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.lang.IllegalStateException
import java.lang.Integer.max
import java.lang.Integer.min

/*
--- Day 17: Reservoir Research ---

You arrive in the year 18. If it weren't for the coat you got in 1018,
you would be very cold: the North Pole base hasn't even been constructed.

Rather, it hasn't been constructed yet. The Elves are making a little progress,
but there's not a lot of liquid water in this climate, so they're getting very dehydrated.
Maybe there's more underground?

You scan a two-dimensional vertical slice of the ground nearby and discover that it is mostly sand with veins of clay.
The scan only provides data with a granularity of square meters,
but it should be good enough to determine how much water is trapped there.
In the scan, x represents the distance to the right, and y represents the distance down.
There is also a spring of water near the surface at x=500, y=0.
The scan identifies which square meters are clay (your puzzle input).

For example, suppose your scan shows the following veins of clay:

x=495, y=2..7
y=7, x=495..501
x=501, y=3..7
x=498, y=2..4
x=506, y=1..2
x=498, y=10..13
x=504, y=10..13
y=13, x=498..504

Rendering clay as #, sand as ., and the water spring as +, and with x increasing to the right and y increasing downward,
this becomes:

   44444455555555
   99999900000000
   45678901234567
 0 ......+.......
 1 ............#.
 2 .#..#.......#.
 3 .#..#..#......
 4 .#..#..#......
 5 .#.....#......
 6 .#.....#......
 7 .#######......
 8 ..............
 9 ..............
10 ....#.....#...
11 ....#.....#...
12 ....#.....#...
13 ....#######...

The spring of water will produce water forever. Water can move through sand, but is blocked by clay.
Water always moves down when possible, and spreads to the left and right otherwise,
filling space that has clay on both sides and falling out otherwise.

For example, if five squares of water are created, they will flow downward until they reach the clay and settle there.
Water that has come to rest is shown here as ~,
while sand through which water has passed (but which is now dry again) is shown as |:

......+.......
......|.....#.
.#..#.|.....#.
.#..#.|#......
.#..#.|#......
.#....|#......
.#~~~~~#......
.#######......
..............
..............
....#.....#...
....#.....#...
....#.....#...
....#######...

Two squares of water can't occupy the same location.
If another five squares of water are created, they will settle on the first five, filling the clay reservoir a little more:

......+.......
......|.....#.
.#..#.|.....#.
.#..#.|#......
.#..#.|#......
.#~~~~~#......
.#~~~~~#......
.#######......
..............
..............
....#.....#...
....#.....#...
....#.....#...
....#######...

Water pressure does not apply in this scenario. (!)
If another four squares of water are created, they will stay on the right side of the barrier,
and no water will reach the left side:

......+.......
......|.....#.
.#..#.|.....#.
.#..#~~#......
.#..#~~#......
.#~~~~~#......
.#~~~~~#......
.#######......
..............
..............
....#.....#...
....#.....#...
....#.....#...
....#######...

At this point, the top reservoir overflows.
While water can reach the tiles above the surface of the water, it cannot settle there,
and so the next five squares of water settle like this:

......+.......
......|.....#.
.#..#||||...#.
.#..#~~#|.....
.#..#~~#|.....
.#~~~~~#|.....
.#~~~~~#|.....
.#######|.....
........|.....
........|.....
....#...|.#...
....#...|.#...
....#~~~~~#...
....#######...

Note especially the leftmost |: the new squares of water can reach this tile, but cannot stop there.
Instead, eventually, they all fall to the right and settle in the reservoir below.

After 10 more squares of water, the bottom reservoir is also full:

......+.......
......|.....#.
.#..#||||...#.
.#..#~~#|.....
.#..#~~#|.....
.#~~~~~#|.....
.#~~~~~#|.....
.#######|.....
........|.....
........|.....
....#~~~~~#...
....#~~~~~#...
....#~~~~~#...
....#######...

Finally, while there is nowhere left for the water to settle,
it can reach a few more tiles before overflowing beyond the bottom of the scanned data:

......+.......    (line not counted: above minimum y value)
......|.....#.
.#..#||||...#.
.#..#~~#|.....
.#..#~~#|.....
.#~~~~~#|.....
.#~~~~~#|.....
.#######|.....
........|.....
...|||||||||..
...|#~~~~~#|..
...|#~~~~~#|..
...|#~~~~~#|..
...|#######|..
...|.......|..    (line not counted: below maximum y value)
...|.......|..    (line not counted: below maximum y value)
...|.......|..    (line not counted: below maximum y value)

How many tiles can be reached by the water?
To prevent counting forever, ignore tiles with a y coordinate smaller than the smallest y coordinate in your scan data
or larger than the largest one.
 Any x coordinate is valid. In this example, the lowest y coordinate given is 1, and the highest is 13,
causing the water spring (in row 0) and the water falling off the bottom of the render (in rows 14 through infinity) to be ignored.

So, in the example above, counting both water at rest (~) and other sand tiles the water can hypothetically reach (|),
the total number of tiles the water can reach is 57.

How many tiles can the water reach within the range of y values in your scan?

 */

class Day17Spec : Spek({

    describe("part 1") {
        given("example scan data") {
            val scanData = """
                    x=495, y=2..7
                    y=7, x=495..501
                    x=501, y=3..7
                    x=498, y=2..4
                    x=506, y=1..2
                    x=498, y=10..13
                    x=504, y=10..13
                    y=13, x=498..504
                """.trimIndent()
            val springCoord = GridCoord(500, 0)

            describe("parse ground scan") {
                given("scan data line") {
                    val scanDataLine = "x=495, y=2..7"
                    it("should be parsed correctly") {
                        val scanLine = parseScanData(scanDataLine)
                        scanLine `should equal` ScanData(IntRange(495, 495), IntRange(2, 7))
                    }
                }
                it("should parse scan data correctly") {
                    val scan = processScanData(parseGroundScan(scanData), springCoord)
                    scan.toString() `should equal` """
                        ......+.......
                        ............#.
                        .#..#.......#.
                        .#..#..#......
                        .#..#..#......
                        .#.....#......
                        .#.....#......
                        .#######......
                        ..............
                        ..............
                        ....#.....#...
                        ....#.....#...
                        ....#.....#...
                        ....#######...

                        """.trimIndent()
                }
            }
            describe("hasBorder to decide if something has to be filled") {
                given("example scan") {
                    val scan = processScanData(parseGroundScan(scanData), springCoord)

                    it("should has find a border on the left side") {
                        val coord = GridCoord(500, 6)
                        hasBorder(coord, -1, scan) `should equal` true
                    }
                    it("should has find a border on the right side") {
                        val coord = GridCoord(500, 6)
                        hasBorder(coord, 1, scan) `should equal` true
                    }
                }
            }
            describe("let the water flow") {
                it(" should have water flow down") {
                    val scan = processScanData(parseGroundScan(scanData), springCoord)
                    val result = flowDown(springCoord, scan)
                    scan.toString() `should equal` """
                        ......+.......
                        ......|.....#.
                        .#..#.|.....#.
                        .#..#.|#......
                        .#..#.|#......
                        .#....|#......
                        .#....|#......
                        .#######......
                        ..............
                        ..............
                        ....#.....#...
                        ....#.....#...
                        ....#.....#...
                        ....#######...

                        """.trimIndent()
                    result `should equal` GridCoord(500, 6)
                }
                it(" should process one step") {
                    val scan = processScanData(parseGroundScan(scanData), springCoord)
                    val result = processOneStep(listOf(springCoord), scan)
                    scan.toString() `should equal` """
                        ......+.......
                        ......|.....#.
                        .#..#.|.....#.
                        .#..#~~#......
                        .#..#~~#......
                        .#~~~~~#......
                        .#~~~~~#......
                        .#######......
                        ..............
                        ..............
                        ....#.....#...
                        ....#.....#...
                        ....#.....#...
                        ....#######...

                        """.trimIndent()
                    result `should equal` listOf(GridCoord(500, 2))
                }
            }
        }
    }
})

fun processOneStep(coords: List<GridCoord>, scan: GroundScan): List<GridCoord> {
    val afterFlowDown = coords.mapNotNull {
        flowDown(it, scan)
    }
    return afterFlowDown.map {
        fillWithWater(it, scan)
    }
}

fun fillWithWater(coord: GridCoord, scan: GroundScan): GridCoord {
    fun fillLine(coord: GridCoord, dir: Int) {
        val range = if (dir < 0) coord.x downTo scan.xOffset
        else coord.x..scan.maxX
        for (x in range) {
            if (scan[GridCoord(x, coord.y)] == GroundGridElement.CLAY)
                return
            else scan[GridCoord(x, coord.y)] = GroundGridElement.WATER
        }
    }
    var currCoord = coord
    while (hasBorder(currCoord, 1, scan) && hasBorder(currCoord, -1, scan)) {
        fillLine(currCoord, 1)
        fillLine(currCoord, -1)
        currCoord = GridCoord(currCoord.x, currCoord.y - 1)
    }
    return currCoord
}

fun hasBorder(coord: GridCoord, dir: Int, scan: GroundScan): Boolean {
    val range = if (dir < 0) coord.x downTo scan.xOffset
    else coord.x..scan.maxX
    for (x in range) {
        if (scan[GridCoord(x, coord.y + 1)] !in setOf(GroundGridElement.CLAY, GroundGridElement.WATER))
            return false
        if (scan[GridCoord(x, coord.y)] == GroundGridElement.CLAY)
            return true
    }
    throw IllegalStateException("Unexpected scan data in line ${coord.y}")
}

fun flowDown(coord: GridCoord, scan: GroundScan): GridCoord? {
    var currCoord: GridCoord? = null
    for (y in coord.y+1 .. scan.maxY) {
        if (scan[GridCoord(coord.x, y)] == GroundGridElement.DRY_SAND) {
            currCoord = GridCoord(coord.x, y)
            scan[currCoord] = GroundGridElement.WET_SAND
        } else return currCoord
    }
    return currCoord
}

fun processScanData(scanDatas: List<ScanData>, springCoord: GridCoord): GroundScan {
    val maxXScanData =  scanDatas.map { it.xRange.last }.max()!!
    val maxX = max(maxXScanData, springCoord.x)
    val minXScanData =  scanDatas.map { it.xRange.first }.min()!!
    val minX = min(minXScanData, springCoord.x)
    val maxY =  scanDatas.map { it.yRange.last }.max()!!
    val xOffset = minX - 1
    val grid = Array(maxY + 1) { Array(maxX - xOffset + 2) { GroundGridElement.DRY_SAND } }
    val result = GroundScan(xOffset, grid, maxX, maxY)
    scanDatas.forEach { scanData ->
        scanData.xRange.forEach { x ->
            scanData.yRange.forEach { y ->
                result[GridCoord(x, y)] =  GroundGridElement.CLAY
            }
        }
    }
    result[springCoord] = GroundGridElement.SPRING
    return result
}

data class GroundScan(val xOffset: Int = 0, val grid: Array<Array<GroundGridElement>> = emptyArray(), val maxX: Int, val maxY: Int) {
    operator fun get(coord: GridCoord) = grid[coord.y][coord.x-xOffset]
    operator fun set(coord: GridCoord, element: GroundGridElement) {
        grid[coord.y][coord.x-xOffset] = element
    }
    override fun toString() =
            grid.map { row ->
                row.map { element -> element.toString()}
                        .joinToString("", postfix = "\n")
            }.joinToString("")
}

enum class GroundGridElement(val c: Char) {
    DRY_SAND('.'),
    WET_SAND('|'),
    CLAY('#'),
    WATER('~'),
    SPRING('+');

    override fun toString() = c.toString()
}

data class GridCoord(val x: Int, val y: Int)


fun parseGroundScan(scanData: String): List<ScanData> =
        scanData.split("\n")
                .map { parseScanData(it) }

fun parseScanData(scanLine: String): ScanData {
    val parts = scanLine.split(",").map { it.trim() }
    val coordMap = parts.map {coordData ->
        val splitExpression = coordData.split("=").map { it.trim() }
        val coord = splitExpression[0]
        val data = parseScanRange(splitExpression[1])
        coord to data
    }.toMap()
    return ScanData(coordMap["x"]!!, coordMap["y"]!!)
}

fun parseScanRange(rangeString: String): IntRange {
    val parts = rangeString.split("..").map { it.trim() }
    return if (parts.size > 1) IntRange(parts[0].toInt(), parts[1].toInt())
    else IntRange(parts[0].toInt(), parts[0].toInt())
}

data class ScanData(val xRange: IntRange, val yRange: IntRange)
