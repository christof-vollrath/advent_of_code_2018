import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 22: Mode Maze ---

This is it, your final stop: the year -483. It's snowing and dark outside;
the only light you can see is coming from a small cottage in the distance.
You make your way there and knock on the door.

A portly man with a large, white beard answers the door and invites you inside.
For someone living near the North Pole in -483, he must not get many visitors, but he doesn't act surprised to see you.
Instead, he offers you some milk and cookies.

After talking for a while, he asks a favor of you.
His friend hasn't come back in a few hours, and he's not sure where he is.
Scanning the region briefly, you discover one life signal in a cave system nearby;
his friend must have taken shelter there.
The man asks if you can go there to retrieve his friend.

The cave is divided into square regions which are either dominantly rocky, narrow, or wet (called its type).
Each region occupies exactly one coordinate in X,Y format where X and Y are integers and zero or greater.
(Adjacent regions can be the same type.)

The scan (your puzzle input) is not very detailed:
it only reveals the depth of the cave system and the coordinates of the target.
However, it does not reveal the type of each region. The mouth of the cave is at 0,0.

The man explains that due to the unusual geology in the area,
there is a method to determine any region's type based on its erosion level.
The erosion level of a region can be determined from its geologic index.

The geologic index can be determined using the first rule that applies from the list below:

The region at 0,0 (the mouth of the cave) has a geologic index of 0.
The region at the coordinates of the target has a geologic index of 0.
If the region's Y coordinate is 0, the geologic index is its X coordinate times 16807.
If the region's X coordinate is 0, the geologic index is its Y coordinate times 48271.
Otherwise, the region's geologic index is the result of multiplying the erosion levels of the regions at X-1,Y and X,Y-1.

A region's erosion level is its geologic index plus the cave system's depth, all modulo 20183. Then:
If the erosion level modulo 3 is 0, the region's type is rocky.
If the erosion level modulo 3 is 1, the region's type is wet.
If the erosion level modulo 3 is 2, the region's type is narrow.

For example, suppose the cave system's depth is 510 and the target's coordinates are 10,10.

Using % to represent the modulo operator, the cavern would look as follows:

At 0,0, the geologic index is 0. The erosion level is (0 + 510) % 20183 = 510. The type is 510 % 3 = 0, rocky.
At 1,0, because the Y coordinate is 0, the geologic index is 1 * 16807 = 16807.
The erosion level is (16807 + 510) % 20183 = 17317. The type is 17317 % 3 = 1, wet.
At 0,1, because the X coordinate is 0, the geologic index is 1 * 48271 = 48271.
The erosion level is (48271 + 510) % 20183 = 8415. The type is 8415 % 3 = 0, rocky.
At 1,1, neither coordinate is 0 and it is not the coordinate of the target,
so the geologic index is the erosion level of 0,1 (8415) times the erosion level of 1,0 (17317),
8415 * 17317 = 145722555. The erosion level is (145722555 + 510) % 20183 = 1805. The type is 1805 % 3 = 2, narrow.
At 10,10, because they are the target's coordinates, the geologic index is 0.
The erosion level is (0 + 510) % 20183 = 510. The type is 510 % 3 = 0, rocky.

Drawing this same cave system with rocky as ., wet as =, narrow as |,
the mouth as M, the target as T, with 0,0 in the top-left corner,
X increasing to the right, and Y increasing downward, the top-left corner of the map looks like this:

M=.|=.|.|=.|=|=.
.|=|=|||..|.=...
.==|....||=..|==
=.|....|.==.|==.
=|..==...=.|==..
=||.=.=||=|=..|=
|.=.===|||..=..|
|..==||=.|==|===
.=..===..=|.|||.
.======|||=|=.|=
.===|=|===T===||
=|||...|==..|=.|
=.=|=.=..=.||==|
||=|=...|==.=|==
|=.=||===.|||===
||.|==.|.|.||=||

Before you go in, you should determine the risk level of the area.
For the rectangle that has a top-left corner of region 0,0
and a bottom-right corner of the region containing the target,
add up the risk level of each individual region: 0 for rocky regions, 1 for wet regions, and 2 for narrow regions.

In the cave system above, because the mouth is at 0,0 and the target is at 10,10,
adding up the risk level of all regions with an X coordinate from 0 to 10 and a Y coordinate from 0 to 10,
this total is 114.

What is the total risk level for the smallest rectangle that includes 0,0 and the target's coordinates?

To begin, get your puzzle input.

depth: 3879
target: 8,713
 */

class Day22Spec : Spek({

    describe("part 1") {
        describe("calculate geologic index") {
            given("example input") {
                val testData = arrayOf(
                        data(Coord(0, 0), 0L, 510L),
                        data(Coord(1, 0), 16807L, 17317L),
                        data(Coord(0, 1), 48271L, 8415L),
                        data(Coord(1, 1), 145722555L, 1805L),
                        data(Coord(10, 10), 0L, 510L)
                )
                onData("for %s it should calculate the right geologic index and erosion level", with = *testData) { input, expectedGelologicIndex, expectedErosionLevel ->
                    val geologicIndex = geologicIndex(input, Coord(10, 10), 510)
                    it("should calculate geologic index as $expectedGelologicIndex") {
                        geologicIndex `should equal` expectedGelologicIndex
                    }
                    it("should calculate erosion level as $expectedErosionLevel") {
                        val erosionLevel = erosionLevel(geologicIndex, 510)
                        erosionLevel `should equal` expectedErosionLevel
                    }
                }
            }
        }
    }
})

fun geologicIndex(coord: Coord, target: Coord, depth: Int): Long = when {
    coord == Coord(0, 0) || coord == target -> 0L
    coord.y == 0 -> coord.x * 16807L
    coord.x == 0 -> coord.y * 48271L
    else -> erosionLevel(geologicIndex(Coord(coord.x-1, coord.y), target, depth), depth) *
            erosionLevel(geologicIndex(Coord(coord.x, coord.y-1), target, depth), depth)
}

fun erosionLevel(geologicIndex: Long, depth: Int) = (geologicIndex + depth) % 20183