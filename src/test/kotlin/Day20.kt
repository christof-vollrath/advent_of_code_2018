import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.Integer.max
import java.lang.Integer.min

/*
--- Day 20: A Regular Map ---

While you were learning about instruction pointers, the Elves made considerable progress.
When you look up, you discover that the North Pole base construction project has completely surrounded you.

The area you are in is made up entirely of rooms and doors.
The rooms are arranged in a grid, and rooms only connect to adjacent rooms when a door is present between them.

For example, drawing rooms as ., walls as #, doors as | or -, your current position as X, and where north is up,
the area you're in might look like this:

#####
#.|.#
#-###
#.|X#
#####

You get the attention of a passing construction Elf and ask for a map.
"I don't have time to draw out a map of this place - it's huge.
Instead, I can give you directions to every room in the facility!"
He writes down some directions on a piece of parchment and runs off.
In the example above, the instructions might have been ^WNE$, a regular expression or "regex" (your puzzle input).

The regex matches routes (like WNE for "west, north, east") that will take you from your current room
through various doors in the facility.
In aggregate, the routes will take you through every door in the facility at least once;
mapping out all of these routes will let you build a proper map and find your way around.

^ and $ are at the beginning and end of your regex;
these just mean that the regex doesn't match anything outside the routes it describes.
(Specifically, ^ matches the start of the route, and $ matches the end of it.)
These characters will not appear elsewhere in the regex.

The rest of the regex matches various sequences of the characters N (north), S (south), E (east), and W (west).
In the example above, ^WNE$ matches only one route, WNE, which means you can move west, then north,
then east from your current position.
Sequences of letters like this always match that exact route in the same order.

Sometimes, the route can branch. A branch is given by a list of options separated by pipes (|)
and wrapped in parentheses.
So, ^N(E|W)N$ contains a branch: after going north, you must choose to go either east or west
before finishing your route by going north again.
By tracing out the possible routes after branching, you can determine where the doors are and,
therefore, where the rooms are in the facility.

For example, consider this regex: ^ENWWW(NEEE|SSE(EE|N))$

This regex begins with ENWWW, which means that from your current position,
all routes must begin by moving east, north, and then west three times, in that order.
After this, there is a branch.
Before you consider the branch, this is what you know about the map so far,
 with doors you aren't sure about marked with a ?:

#?#?#?#?#
?.|.|.|.?
#?#?#?#-#
    ?X|.?
    #?#?#

After this point, there is (NEEE|SSE(EE|N)).
This gives you exactly two options: NEEE and SSE(EE|N).
By following NEEE, the map now looks like this:

#?#?#?#?#
?.|.|.|.?
#-#?#?#?#
?.|.|.|.?
#?#?#?#-#
    ?X|.?
    #?#?#

Now, only SSE(EE|N) remains. Because it is in the same parenthesized group as NEEE,
it starts from the same room NEEE started in.
It states that starting from that point, there exist doors which will allow you to move south twice, then east;
this ends up at another branch. After that, you can either move east twice or north once.
This information fills in the rest of the doors:

#?#?#?#?#
?.|.|.|.?
#-#?#?#?#
?.|.|.|.?
#-#?#?#-#
?.?.?X|.?
#-#-#?#?#
?.|.|.|.?
#?#?#?#?#

Once you've followed all possible routes, you know the remaining unknown parts are all walls,
producing a finished map of the facility:

#########
#.|.|.|.#
#-#######
#.|.|.|.#
#-#####-#
#.#.#X|.#
#-#-#####
#.|.|.|.#
#########

Sometimes, a list of options can have an empty option, like (NEWS|WNSE|).
This means that routes at this point could effectively skip the options in parentheses and move on immediately.
For example, consider this regex and the corresponding map:

^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$

###########
#.|.#.|.#.#
#-###-#-#-#
#.|.|.#.#.#
#-#####-#-#
#.#.#X|.#.#
#-#-#####-#
#.#.|.|.|.#
#-###-###-#
#.|.|.#.|.#
###########

This regex has one main route which, at three locations, can optionally include additional detours and be valid:
(NEWS|), (WNSE|), and (SWEN|).
Regardless of which option is taken, the route continues from the position it is left at after taking those steps.
So, for example, this regex matches all of the following routes (and more that aren't listed here):

ENNWSWWSSSEENEENNN
ENNWSWWNEWSSSSEENEENNN
ENNWSWWNEWSSSSEENEESWENNNN
ENNWSWWSSSEENWNSEEENNN

By following the various routes the regex matches, a full map of all of the doors
and rooms in the facility can be assembled.

To get a sense for the size of this facility, you'd like to determine which room is furthest from you:
specifically, you would like to find the room for which the shortest path to that room would
require passing through the most doors.

In the first example (^WNE$), this would be the north-east corner 3 doors away.
In the second example (^ENWWW(NEEE|SSE(EE|N))$), this would be the south-east corner 10 doors away.
In the third example (^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$), this would be the north-east corner 18 doors away.
Here are a few more examples:

Regex: ^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$
Furthest room requires passing 23 doors

#############
#.|.|.|.|.|.#
#-#####-###-#
#.#.|.#.#.#.#
#-#-###-#-#-#
#.#.#.|.#.|.#
#-#-#-#####-#
#.#.#.#X|.#.#
#-#-#-###-#-#
#.|.#.|.#.#.#
###-#-###-#-#
#.|.#.|.|.#.#
#############

Regex: ^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$
Furthest room requires passing 31 doors

###############
#.|.|.|.#.|.|.#
#-###-###-#-#-#
#.|.#.|.|.#.#.#
#-#########-#-#
#.#.|.|.|.|.#.#
#-#-#########-#
#.#.#.|X#.|.#.#
###-#-###-#-#-#
#.|.#.#.|.#.|.#
#-###-#####-###
#.|.#.|.|.#.#.#
#-#-#####-#-#-#
#.#.|.|.|.#.|.#
###############

What is the largest number of doors you would be required to pass through to reach a room?
That is, find the room for which the shortest path from your starting location to that room would require
passing through the most doors; what is the fewest doors you can pass through to reach it?

--- Part Two ---
Okay, so the facility is big.

How many rooms have a shortest path from your current location that pass through at least 1000 doors?

 */

fun findFurthestRoom(input: String): Pair<Coord, Int> = findFurthestRoom(calculatePathes(parseAndExecuteMapInstructions(input)))

fun findFurthestRoom(pathes: List<Pair<Coord, List<Directions>>>): Pair<Coord, Int> =
        pathes.map { (coord, path) ->
            coord to path.size
        }
        .sortedByDescending { it.second }
        .first()

fun calculatePathes(roomMap: RoomMap): List<Pair<Coord, List<Directions>>> {
    val interimResult = mutableMapOf(Coord(0, 0) to emptyList<Directions>())
    calculatePathes(roomMap, listOf(Coord(0, 0)), interimResult)
    return interimResult.toList()
}

tailrec fun calculatePathes(roomMap: RoomMap, current: List<Coord>, interimResult: MutableMap<Coord, List<Directions>>) {
    val next = current.flatMap { currentCoord ->
        val nextStep = enumValues<Directions>().map { dir ->
            dir to moveCoord(currentCoord, dir)
        }.filter { (_, nextCoord) ->
            roomMap.doors.contains(Door(currentCoord, nextCoord)) // There is a door
        }.filter { (_, nextCoord) ->
            ! interimResult.contains(nextCoord) // Already visited
        }
        val currentPath = interimResult[currentCoord] ?: throw IllegalStateException("Must have $currentCoord in interim results")
        nextStep.forEach { (dir, nextCoord) ->
            interimResult[nextCoord] = currentPath + dir
        }
        nextStep.map { (_, nextCoord) -> nextCoord }
    }
    if (next.isNotEmpty()) calculatePathes(roomMap, next, interimResult)
}

fun moveCoord(coord: Coord, direction: Directions): Coord {
    fun move(coord: Coord, dx: Int, dy: Int) = Coord(coord.x + dx, coord.y + dy)
    return when (direction) {
        Directions.NORTH -> move(coord,  0, -1)
        Directions.EAST -> move(coord,  1,  0)
        Directions.SOUTH -> move(coord,  0,  1)
        Directions.WEST -> move(coord, -1,  0)
    }
}

enum class Directions { NORTH, EAST, SOUTH, WEST }

sealed class MapInstruction {
    abstract fun execute(start: Set<Coord>, roomMap: RoomMap): Set<Coord>
}

data class SequenceInstruction(val instructions: List<MapInstruction>) : MapInstruction() {
    override fun  execute(start: Set<Coord>, roomMap: RoomMap): Set<Coord> = instructions.fold(start) { current, instruction ->
        instruction.execute(current, roomMap)
    }
}

data class MoveInstruction(val directions: List<Directions>) : MapInstruction() {
    override fun execute(start: Set<Coord>, roomMap: RoomMap): Set<Coord> {
        return start.map { coord: Coord ->
            directions.fold(coord) { current, dir ->
                val next = moveCoord(current, dir)
                roomMap.addDoor(current, next)
                next
            }
        }.toSet()
    }
}

data class BranchInstruction(val instructions: List<SequenceInstruction>) : MapInstruction() {
    override fun execute(start: Set<Coord>, roomMap: RoomMap): Set<Coord> = instructions.flatMap { it.execute(start, roomMap) }.toSet()
}

data class ParserState(var pos: Int = 0)

fun parseMapInstructions(input: String, parserState: ParserState = ParserState()): SequenceInstruction {
    if (input.getOrNull(parserState.pos) != '^') throw IllegalArgumentException("Expected ^ at the beginning")
    parserState.pos++
    val result = parseSequenceInstruction(input, parserState)
    if (input.getOrNull(parserState.pos) != '$') throw IllegalArgumentException("Expected ^ at the end")
    return result
}


fun parseSequenceInstruction(input: String, parserState: ParserState): SequenceInstruction {
    val instructions = sequence {
        while (parserState.pos < input.length) {
            val mapInstruction = parseMapInstruction(input, parserState)
            if (mapInstruction != null) yield(mapInstruction)
            else break // parserState.pos++ // Ignore unhandled input
        }
    }.toList().filterNotNull()
    return SequenceInstruction(instructions)
}

fun parseMapInstruction(input: String, parserState: ParserState): MapInstruction? =
        if (parserState.pos < input.length) {
            if (input[parserState.pos] == '(')  {
                parserState.pos++ // consume (
                parseBranchInstruction(input, parserState)
            } else parseMoveInstruction(input, parserState)
        } else null

fun parseBranchInstruction(input: String, parserState: ParserState): BranchInstruction {
    val instructions = sequence {
        handlechars@ while (parserState.pos < input.length) {
            yield(parseSequenceInstruction(input, parserState))
            if (parserState.pos < input.length) {
                when (input[parserState.pos]) {
                    '|' -> parserState.pos++ // skip to next
                    ')' -> { parserState.pos++
                        break@handlechars // Done with this branch
                    }
                }
            }
        }
    }.toList()
    return BranchInstruction(instructions)
}

fun parseMoveInstruction(input: String, parserState: ParserState): MoveInstruction? {
    val directions = sequence {
        handlechars@ while (parserState.pos < input.length) {
            when (input[parserState.pos]) {
                'N' -> yield(Directions.NORTH)
                'E' -> yield(Directions.EAST)
                'S' -> yield(Directions.SOUTH)
                'W' -> yield(Directions.WEST)
                else -> break@handlechars
            }
            parserState.pos++
        }
    }.toList()
    return if (directions.isNotEmpty()) MoveInstruction(directions)
    else null
}

fun parseAndExecuteMapInstructions(input: String): RoomMap {
    val roomMap = RoomMap(emptySet())
    parseMapInstructions(input).execute(setOf(Coord(0, 0)), roomMap)
    return roomMap
}

data class RoomMap(var doors: Set<Door>) {
    var minXY: Coord = Coord(0, 0)
    var maxXY: Coord = Coord(0, 0)
    fun addDoor(from: Coord, to: Coord) {
        minXY = Coord(min(minXY.x, to.x), min(minXY.y, to.y))
        maxXY = Coord(max(maxXY.x, to.x), max(maxXY.y, to.y))
        doors = doors + Door(from, to) + Door(to, from)
    }
    override fun toString(): String {
        val lineWithNoDoors = "#".repeat((maxXY.x - minXY.x + 1) * 2 + 1)
        return lineWithNoDoors + "\n" +
                (minXY.y .. maxXY.y).map { y ->
                    "#" +
                            (minXY.x .. maxXY.x).map { x ->
                                (if(x == 0 && y == 0) "X" else ".") +
                                        if (x < maxXY.x)
                                            if (doors.contains(Door(Coord(x, y), Coord(x+1, y))))
                                                "|" else "#"
                                        else ""
                            }.joinToString("") + "#" + "\n" +
                            "#" +
                            (minXY.x .. maxXY.x).map { x ->
                                (if (y < maxXY.y && doors.contains(Door(Coord(x, y), Coord(x, y+1))))
                                    "-"
                                else "#") + "#"
                            }.joinToString("")
                }.joinToString("\n") + "\n"
    }
}

data class Door(val from: Coord, val to: Coord)

class Day20Spec : Spek({

    describe("part 1 + 2") {
        describe("parse instructions") {
            given("empty instructions") {
                val input = "^$"
                it("should parse correctly") {
                    parseMapInstructions(input) `should equal` SequenceInstruction(emptyList())
                }
            }
            given("simple example") {
                val input = "^WNE$"
                it("should parse correctly") {
                    parseMapInstructions(input) `should equal` SequenceInstruction(listOf(MoveInstruction(listOf(Directions.WEST, Directions.NORTH, Directions.EAST))))
                }
            }
            given("simple example with brackets") {
                val input = "^(WNE)$"
                it("should parse correctly") {
                    parseMapInstructions(input) `should equal` SequenceInstruction(listOf(
                            BranchInstruction(listOf(SequenceInstruction(listOf(MoveInstruction(listOf(Directions.WEST, Directions.NORTH, Directions.EAST))))))
                    ))
                }
            }
            given("example with brackets with directions before and after") {
                val input = "^W(N)E$"
                it("should parse correctly") {
                    parseMapInstructions(input) `should equal` SequenceInstruction(listOf(
                            MoveInstruction(listOf(Directions.WEST)),
                            BranchInstruction(listOf(SequenceInstruction(listOf(MoveInstruction(listOf(Directions.NORTH)))))),
                            MoveInstruction(listOf(Directions.EAST))
                    ))
                }
            }
            given("example with branches") {
                val input = "^ENWWW(NEEE|SSE(EE|N))$"
                it("should parse correctly") {
                    parseMapInstructions(input) `should equal` SequenceInstruction(listOf(
                            MoveInstruction(listOf(Directions.EAST, Directions.NORTH, Directions.WEST, Directions.WEST, Directions.WEST)),
                            BranchInstruction(listOf(
                                    SequenceInstruction(listOf(MoveInstruction(listOf(Directions.NORTH, Directions.EAST, Directions.EAST, Directions.EAST)))),
                                    SequenceInstruction(listOf(
                                            MoveInstruction(listOf(Directions.SOUTH, Directions.SOUTH, Directions.EAST)),
                                            BranchInstruction(listOf(
                                                    SequenceInstruction(listOf(MoveInstruction(listOf(Directions.EAST, Directions.EAST)))),
                                                    SequenceInstruction(listOf(MoveInstruction(listOf(Directions.NORTH))))
                                            ))
                                    ))
                            ))
                    ))
                }
            }
            given("example with an empty branch") {
                val input = "^W(N|)$"
                it("should parse correctly") {
                    parseMapInstructions(input) `should equal` SequenceInstruction(listOf(
                            MoveInstruction(listOf(Directions.WEST)),
                            BranchInstruction(listOf(
                                    SequenceInstruction(listOf(MoveInstruction(listOf(Directions.NORTH)))),
                                    SequenceInstruction(listOf())
                            ))
                    ))
                }
            }

        }
        describe("parse and execute instructions") {
            given("empty instructions") {
                val input = "^$"
                it("should parse and print empty map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                        ###
                        #X#
                        ###
                        
                        """.trimIndent()
                }
            }
            given("simple example") {
                val input = "^WNE$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                        #####
                        #.|.#
                        #-###
                        #.|X#
                        #####
                        
                        """.trimIndent()
                }
            }
            given("simple example with brackets") {
                val input = "^(WNE)$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                        """
                        #####
                        #.|.#
                        #-###
                        #.|X#
                        #####
                        
                        """.trimIndent()
                }
            }
            given("example with branches 1") {
                val input = "^ENWWW$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                            #########
                            #.|.|.|.#
                            #######-#
                            #.#.#X|.#
                            #########
                            
                            """.trimIndent()
                }
            }
            given("example with branches 2") {
                val input = "^ENWWW(NEEE)$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                            #########
                            #.|.|.|.#
                            #-#######
                            #.|.|.|.#
                            #######-#
                            #.#.#X|.#
                            #########
                            
                            """.trimIndent()
                }
            }
            given("example with branches 3") {
                val input = "^ENWWW(NEEE|SSE(EE))$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                            #########
                            #.|.|.|.#
                            #-#######
                            #.|.|.|.#
                            #-#####-#
                            #.#.#X|.#
                            #-#######
                            #.|.|.|.#
                            #########
                            
                            """.trimIndent()
                }
            }
            given("example with branches") {
                val input = "^ENWWW(NEEE|SSE(EE|N))$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                            #########
                            #.|.|.|.#
                            #-#######
                            #.|.|.|.#
                            #-#####-#
                            #.#.#X|.#
                            #-#-#####
                            #.|.|.|.#
                            #########
                            
                            """.trimIndent()
                }
            }
            given("example with branches and empty branch") {
                val input = "^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$"
                it("should parse and print map correctly") {
                    parseAndExecuteMapInstructions(input).toString() `should equal`
                            """
                            ###########
                            #.|.#.|.#.#
                            #-###-#-#-#
                            #.|.|.#.#.#
                            #-#####-#-#
                            #.#.#X|.#.#
                            #-#-#####-#
                            #.#.|.|.|.#
                            #-###-###-#
                            #.|.|.#.|.#
                            ###########
                            
                            """.trimIndent()
                }
            }
        }
        describe("find furthest room") {
            given("example input") {
                val testData = arrayOf(
                        data("^WNE$", Coord(0, -1) to 3),
                        data("^ENWWW(NEEE|SSE(EE|N))$", Coord(1, 1) to 10),
                        data("^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$", Coord(2, -2) to 18),
                        data("^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$", Coord(-1, -2) to 23),
                        data("^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$", Coord(0, 1) to 31)
                )

                onData("find furthest room for %s", with = *testData) { input, expected ->
                    val result = findFurthestRoom(input)
                    it("returns $expected") {
                        result `should equal` expected
                    }
                }

            }
        }
        describe("exercise 1 + 2") {
            given("exercise input") {
                val input = readResource("day20Input.txt")
                it("should build map correctly") {
                    println(parseAndExecuteMapInstructions(input))
                }
                on("find furthest room") {
                    val roomMap = parseAndExecuteMapInstructions(input)
                    val pathes = calculatePathes(roomMap)
                    val result = findFurthestRoom(pathes)
                    it("should find furthest room") {
                        result `should equal` (Coord(x=42, y=34) to 3806)
                    }
                    it("should find rooms with path >= 1000 (part 2)") {
                        val rooms1000 = pathes.filter { (coord, path) -> path.size >= 1000}
                        rooms1000.size `should equal` 8354
                    }
                }
            }
        }
    }

})
