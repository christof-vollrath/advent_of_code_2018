import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.lang.IllegalArgumentException

/*
--- Day 15: Beverage Bandits ---

Having perfected their hot chocolate, the Elves have a new problem:
the Goblins that live in these caves will do anything to steal it.
Looks like they're here for a fight.

You scan the area, generating a map of the walls (#), open cavern (.),
and starting position of every Goblin (G) and Elf (E) (your puzzle input).

Combat proceeds in rounds; in each round, each unit that is still alive takes a turn,
resolving all of its actions before the next unit's turn begins. On each unit's turn,
it tries to move into range of an enemy (if it isn't already) and then attack (if it is in range).

All units are very disciplined and always follow very strict combat rules.
Units never move or attack diagonally, as doing so would be dishonorable.
When multiple choices are equally valid, ties are broken in reading order: top-to-bottom, then left-to-right.
For instance, the order in which units take their turns within a round is the reading order
of their starting positions in that round,
regardless of the type of unit or whether other units have moved after the round started.

For example:

                 would take their
These units:   turns in this order:
  #######           #######
  #.G.E.#           #.1.2.#
  #E.G.E#           #3.4.5#
  #.G.E.#           #.6.7.#
  #######           #######

Each unit begins its turn by identifying all possible targets (enemy units). If no targets remain, combat ends.

Then, the unit identifies all of the open squares (.) that are in range of each target;
these are the squares which are adjacent (immediately up, down, left, or right)
to any target and which aren't already occupied by a wall or another unit.
Alternatively, the unit might already be in range of a target.
If the unit is not already in range of a target, and there are no open squares which are in range of a target,
the unit ends its turn.

If the unit is already in range of a target, it does not move, but continues its turn with an attack.
Otherwise, since it is not in range of a target, it moves.

To move, the unit first considers the squares that are in range and determines which of those squares it
could reach in the fewest steps.
A step is a single move to any adjacent (immediately up, down, left, or right) open (.) square.
Units cannot move into walls or other units.
The unit does this while considering the current positions of units
and does not do any prediction about where units will be later.
If the unit cannot reach (find an open path to) any of the squares that are in range, it ends its turn.
If multiple squares are in range and tied for being reachable in the fewest steps,
the square which is first in reading order is chosen.

For example:

Targets:      In range:     Reachable:    Nearest:      Chosen:
#######       #######       #######       #######       #######
#E..G.#       #E.?G?#       #E.@G.#       #E.!G.#       #E.+G.#
#...#.#  -->  #.?.#?#  -->  #.@.#.#  -->  #.!.#.#  -->  #...#.#
#.G.#G#       #?G?#G#       #@G@#G#       #!G.#G#       #.G.#G#
#######       #######       #######       #######       #######

In the above scenario, the Elf has three targets (the three Goblins):

Each of the Goblins has open, adjacent squares which are in range (marked with a ? on the map).
Of those squares, four are reachable (marked @);
the other two (on the right) would require moving through a wall or unit to reach.
Three of these reachable squares are nearest, requiring the fewest steps (only 2) to reach (marked !).
Of those, the square which is first in reading order is chosen (+).

The unit then takes a single step toward the chosen square along the shortest path to that square.
If multiple steps would put the unit equally closer to its destination,
the unit chooses the step which is first in reading order.
(This requires knowing when there is more than one shortest path so that you can consider
the first step of each such path.)

For example:

In range:     Nearest:      Chosen:       Distance:     Step:
#######       #######       #######       #######       #######
#.E...#       #.E...#       #.E...#       #4E212#       #..E..#
#...?.#  -->  #...!.#  -->  #...+.#  -->  #32101#  -->  #.....#
#..?G?#       #..!G.#       #...G.#       #432G2#       #...G.#
#######       #######       #######       #######       #######

The Elf sees three squares in range of a target (?), two of which are nearest (!),
and so the first in reading order is chosen (+).
 Under "Distance", each open square is marked with its distance from the destination square;
 the two squares to which the Elf could move on this turn (down and to the right) are both equally good moves
 and would leave the Elf 2 steps from being in range of the Goblin.
 Because the step which is first in reading order is chosen, the Elf moves right one square.

Here's a larger example of move:

Initially:
#########
#G..G..G#
#.......#
#.......#
#G..E..G#
#.......#
#.......#
#G..G..G#
#########

After 1 round:
#########
#.G...G.#
#...G...#
#...E..G#
#.G.....#
#.......#
#G..G..G#
#.......#
#########

After 2 rounds:
#########
#..G.G..#
#...G...#
#.G.E.G.#
#.......#
#G..G..G#
#.......#
#.......#
#########

After 3 rounds:
#########
#.......#
#..GGG..#
#..GEG..#
#G..G...#
#......G#
#.......#
#.......#
#########

Once the Goblins and Elf reach the positions above, they all are either in range of a target
or cannot find any square in range of a target, and so none of the units can move until a unit dies.

After moving (or if the unit began its turn in range of a target), the unit attacks.

To attack, the unit first determines all of the targets that are in range of it by being immediately adjacent to it.
If there are no such targets, the unit ends its turn.
Otherwise, the adjacent target with the fewest hit points is selected;
in a tie, the adjacent target with the fewest hit points which is first in reading order is selected.

The unit deals damage equal to its attack power to the selected target,
reducing its hit points by that amount.
If this reduces its hit points to 0 or fewer, the selected target dies: its square becomes .
and it takes no further turns.

Each unit, either Goblin or Elf, has 3 attack power and starts with 200 hit points.

For example, suppose the only Elf is about to attack:

       HP:            HP:
G....  9       G....  9
..G..  4       ..G..  4
..EG.  2  -->  ..E..
..G..  2       ..G..  2
...G.  1       ...G.  1

The "HP" column shows the hit points of the Goblin to the left in the corresponding row.
The Elf is in range of three targets: the Goblin above it (with 4 hit points),
the Goblin to its right (with 2 hit points), and the Goblin below it (also with 2 hit points).
Because three targets are in range, the ones with the lowest hit points are selected:
the two Goblins with 2 hit points each (one to the right of the Elf and one below the Elf).
Of those, the Goblin first in reading order (the one to the right of the Elf) is selected.
The selected Goblin's hit points (2) are reduced by the Elf's attack power (3),
reducing its hit points to -1, killing it.

After attacking, the unit's turn ends. Regardless of how the unit's turn ends,
the next unit in the round takes its turn.
If all units have taken turns in this round, the round ends, and a new round begins.

The Elves look quite outnumbered. You need to determine the outcome of the battle:
the number of full rounds that were completed (not counting the round in which combat ends)
multiplied by the sum of the hit points of all remaining units at the moment combat ends.
(Combat only ends when a unit finds no targets during its turn.)

Below is an entire sample combat. Next to each map, each row's units' hit points are listed from left to right.

Initially:
#######
#.G...#   G(200)
#...EG#   E(200), G(200)
#.#.#G#   G(200)
#..G#E#   G(200), E(200)
#.....#
#######

After 1 round:
#######
#..G..#   G(200)
#...EG#   E(197), G(197)
#.#G#G#   G(200), G(197)
#...#E#   E(197)
#.....#
#######

After 2 rounds:
#######
#...G.#   G(200)
#..GEG#   G(200), E(188), G(194)
#.#.#G#   G(194)
#...#E#   E(194)
#.....#
#######

Combat ensues; eventually, the top Elf dies:

After 23 rounds:
#######
#...G.#   G(200)
#..G.G#   G(200), G(131)
#.#.#G#   G(131)
#...#E#   E(131)
#.....#
#######

After 24 rounds:
#######
#..G..#   G(200)
#...G.#   G(131)
#.#G#G#   G(200), G(128)
#...#E#   E(128)
#.....#
#######

After 25 rounds:
#######
#.G...#   G(200)
#..G..#   G(131)
#.#.#G#   G(125)
#..G#E#   G(200), E(125)
#.....#
#######

After 26 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(122)
#...#E#   E(122)
#..G..#   G(200)
#######

After 27 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(119)
#...#E#   E(119)
#...G.#   G(200)
#######

After 28 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(116)
#...#E#   E(113)
#....G#   G(200)
#######

More combat ensues; eventually, the bottom Elf dies:

After 47 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(59)
#...#.#
#....G#   G(200)
#######

Before the 48th round can finish, the top-left Goblin finds that there are no targets remaining, and so combat ends.
So, the number of full rounds that were completed is 47,
and the sum of the hit points of all remaining units is 200+131+59+200 = 590.
From these, the outcome of the battle is 47 * 590 = 27730.

Here are a few example summarized combats:

#######       #######
#G..#E#       #...#E#   E(200)
#E#E.E#       #E#...#   E(197)
#G.##.#  -->  #.E##.#   E(185)
#...#E#       #E..#E#   E(200), E(200)
#...E.#       #.....#
#######       #######

Combat ends after 37 full rounds
Elves win with 982 total hit points left
Outcome: 37 * 982 = 36334

#######       #######
#E..EG#       #.E.E.#   E(164), E(197)
#.#G.E#       #.#E..#   E(200)
#E.##E#  -->  #E.##.#   E(98)
#G..#.#       #.E.#.#   E(200)
#..E#.#       #...#.#
#######       #######

Combat ends after 46 full rounds
Elves win with 859 total hit points left
Outcome: 46 * 859 = 39514

#######       #######
#E.G#.#       #G.G#.#   G(200), G(98)
#.#G..#       #.#G..#   G(200)
#G.#.G#  -->  #..#..#
#G..#.#       #...#G#   G(95)
#...E.#       #...G.#   G(200)
#######       #######

Combat ends after 35 full rounds
Goblins win with 793 total hit points left
Outcome: 35 * 793 = 27755

#######       #######
#.E...#       #.....#
#.#..G#       #.#G..#   G(200)
#.###.#  -->  #.###.#
#E#G#G#       #.#.#.#
#...#G#       #G.G#G#   G(98), G(38), G(200)
#######       #######

Combat ends after 54 full rounds
Goblins win with 536 total hit points left
Outcome: 54 * 536 = 28944

#########       #########
#G......#       #.G.....#   G(137)
#.E.#...#       #G.G#...#   G(200), G(200)
#..##..G#       #.G##...#   G(200)
#...##..#  -->  #...##..#
#...#...#       #.G.#...#   G(200)
#.G...G.#       #.......#
#.....G.#       #.......#
#########       #########

Combat ends after 20 full rounds
Goblins win with 937 total hit points left
Outcome: 20 * 937 = 18740

What is the outcome of the combat described in your puzzle input?
 */

class Day15Spec : Spek({

    describe("part 1") {
        describe("parse and print fighting area") {
            given("a fighting area") {
                val fightingAreaString = """
                    #######
                    #.G.E.#
                    #E.G.E#
                    #.G.E.#
                    #######
                """.trimIndent()
                val fightingArea = parseFightingArea(fightingAreaString)
                it("should be printed correctly") {
                    fightingArea.print() `should equal` fightingAreaString
                }
            }
        }
        describe("get units in fighting order") {
            given("a fighting area") {
                val fightingArea = parseFightingArea("""
                    #######
                    #.G.E.#
                    #E.G.E#
                    #.G.E.#
                    #######
                """.trimIndent())
                it("should get the right order") {
                    val creatureList = fightingArea.getCreaturesInFightingOrder()
                    creatureList `should equal` listOf(
                            Goblin(2,1 ), Elf(4, 1),
                            Elf(1, 2), Goblin(3, 2), Elf(5, 2),
                            Goblin(2, 3), Elf(4, 3)
                    )
                }
            }
        }
        describe("get targets") {
            given("a fighting area") {
                val fightingArea = parseFightingArea("""
                    #######
                    #E..G.#
                    #...#.#
                    #.G.#G#
                    #######
                """.trimIndent())
                it("should get all targets") {
                    val targetList = (fightingArea[1][1] as Creature).getTargetCreatures(fightingArea)
                    targetList `should equal` listOf(
                            Goblin(4,1 ),
                            Goblin(2, 3), Goblin(5, 3)
                    )
                }
            }
        }
        describe("get adjacent squares for a field") {
            given("a fighting area") {
                val fightingArea = parseFightingArea("""
                    #######
                    #E..G.#
                    #...#.#
                    #.G.#G#
                    #######
                """.trimIndent())
                it("should get all adjacent squares") {
                    val adjacentSquares = (fightingArea[1][1] as Creature).getAdjacentSquares(fightingArea)
                    adjacentSquares `should equal` listOf(Coord(2, 1), Coord(1, 2))
                }
            }
        }
        describe("get target squares to move to") {
            given("a fighting area") {
                val fightingArea = parseFightingArea("""
                    #######
                    #E..G.#
                    #...#.#
                    #.G.#G#
                    #######
                """.trimIndent())
                on("get arget squares") {
                    val targetSquares = (fightingArea[1][1] as Creature).getTargetSquares(fightingArea)
                    it("should get all target squares") {
                        targetSquares `should equal` setOf(
                                Coord(3, 1), Coord(5, 1),
                                Coord(2, 2), Coord(5, 2),
                                Coord(1, 3), Coord(3, 3))
                    }
                    it("should print result") {
                        fightingArea.print(targetSquares, '?') `should equal` """
                            #######
                            #E.?G?#
                            #.?.#?#
                            #?G?#G#
                            #######
                        """.trimIndent()
                    }
                }
            }
        }
        describe("get nearest target squares to move to") {
            given("a fighting area") {
                val fightingArea = parseFightingArea("""
                    #######
                    #E..G.#
                    #...#.#
                    #.G.#G#
                    #######
                """.trimIndent())
                on("should get the nearest target square path") {
                    val targetPath = (fightingArea[1][1] as Creature).getNearestTargetSquarePath(fightingArea)
                    it("should get the right path") {
                        targetPath `should equal` listOf(Coord(2, 1), Coord(3, 1))
                    }
                    it("should print the nearest target square") {
                        fightingArea.print(setOf(targetPath.last()), '+') `should equal` """
                            #######
                            #E.+G.#
                            #...#.#
                            #.G.#G#
                            #######
                        """.trimIndent()
                    }
                }
            }
            given("a fighting area with one elf and one goblin") {
                val fightingArea = parseFightingArea("""
                    #######
                    #.E...#
                    #.....#
                    #...G.#
                    #######
                """.trimIndent())
                on("get the nearest target square for the elf") {
                    val targetPath = (fightingArea[1][2] as Creature).getNearestTargetSquarePath(fightingArea)
                    it("should find the right first step") {
                        targetPath.first() `should equal` Coord(3, 1)
                    }
                }
                on("get the nearest target square for the goblin") {
                    val targetPath = (fightingArea[3][4] as Creature).getNearestTargetSquarePath(fightingArea)
                    it("should find the right first step") {
                        targetPath.first() `should equal` Coord(4, 2)
                    }
                }
            }
            given("a big fighting area with one elf and goblins") {
                val fightingArea = parseFightingArea("""
                    #########
                    #.G...G.#
                    #...G...#
                    #...E..G#
                    #.G.....#
                    #.......#
                    #.......#
                    #G..G..G#
                    #########
                """.trimIndent())
                on("get the nearest target square for one goblin") {
                    val targetPath = (fightingArea[7][1] as Creature).getNearestTargetSquarePath(fightingArea)
                    it("should find the right first step") {
                        targetPath.first() `should equal` Coord(1, 6)
                    }
                }
            }
            given("a fighting area with one elf and one goblin to see one movement") {
                val fightingArea = parseFightingArea("""
                #######
                #.E...#
                #.....#
                #...G.#
                #######
            """.trimIndent())
                on("one movement") {
                    fightingArea.move()
                    it("should have moved") {
                        fightingArea.print() `should equal` """
                            #######
                            #..E..#
                            #...G.#
                            #.....#
                            #######
                        """.trimIndent()
                    }
                    val elf = fightingArea[1][3] as Elf
                    elf.coord.x `should equal` 3
                    elf.coord.y `should equal` 1
                    val goblin = fightingArea[2][4] as Goblin
                    goblin.coord.x `should equal` 4
                    goblin.coord.y `should equal` 2
                }
            }
            given("a fighting area with one elf and one goblin standing close to each other") {
                val fightingArea = parseFightingArea("""
                #######
                #.....#
                #..G..#
                #..E..#
                #######
            """.trimIndent())
                on("one movement") {
                    fightingArea.move()
                    it("should not have moved any creature") {
                        fightingArea.print() `should equal` """
                            #######
                            #.....#
                            #..G..#
                            #..E..#
                            #######
                        """.trimIndent()
                    }
                }
            }
            given("a big fighting area to get three movements") {
                val fightingArea = parseFightingArea("""
                    #########
                    #G..G..G#
                    #.......#
                    #.......#
                    #G..E..G#
                    #.......#
                    #.......#
                    #G..G..G#
                    #########
                """.trimIndent())
                on("moving first times") {
                    fightingArea.move()
                    it("should have the right result after move 1") {
                        fightingArea.print() `should equal` """
                            #########
                            #.G...G.#
                            #...G...#
                            #...E..G#
                            #.G.....#
                            #.......#
                            #G..G..G#
                            #.......#
                            #########
                        """.trimIndent()
                    }
                }
                on("moving second times") {
                    fightingArea.move()
                    it("should have the right result after move 2") {
                        fightingArea.print() `should equal` """
                            #########
                            #..G.G..#
                            #...G...#
                            #.G.E.G.#
                            #.......#
                            #G..G..G#
                            #.......#
                            #.......#
                            #########
                        """.trimIndent()
                    }
                }
                on("moving third times") {
                    fightingArea.move()
                    it("should have the right result after move 3") {
                        fightingArea.print() `should equal` """
                            #########
                            #.......#
                            #..GGG..#
                            #..GEG..#
                            #G..G...#
                            #......G#
                            #.......#
                            #.......#
                            #########
                        """.trimIndent()
                    }
                }
            }
            given("a fighting area to see one elf fighting") {
                val fightingArea = parseFightingArea("""
                    #######
                    #G....#
                    #..G..#
                    #..EG.#
                    #..G..#
                    #...G.#
                    #######
                """.trimIndent())
                (fightingArea[1][1] as Creature).hitPoints = 9
                (fightingArea[2][3] as Creature).hitPoints = 4
                (fightingArea[3][4] as Creature).hitPoints = 2
                (fightingArea[4][3] as Creature).hitPoints = 2
                (fightingArea[5][4] as Creature).hitPoints = 1
                val elf = fightingArea[3][3] as Elf
                val expectedAttackedGoblin = fightingArea[3][4] as Goblin
                on("elf fight") {
                    elf.fight(fightingArea)
                    it("should have fought with goblin and decreased its hit points") {
                        expectedAttackedGoblin.hitPoints `should equal` -1
                    }
                    it("should have removed the killed goblin from the fighting area") {
                        fightingArea.print() `should equal` """
                            #######
                            #G....#
                            #..G..#
                            #..E..#
                            #..G..#
                            #...G.#
                            #######
                        """.trimIndent()
                    }
                }
            }
            given("a fighting area to see some fights") {
                val fightingArea = parseFightingArea("""
                    #######
                    #.G...#
                    #...EG#
                    #.#.#G#
                    #..G#E#
                    #.....#
                    #######
                """.trimIndent())
                on("first fight") {
                    fightingArea.moveAndFight()
                    it("should have fought round 1") {
                        fightingArea.print() `should equal` """
                            #######
                            #..G..#
                            #...EG#
                            #.#G#G#
                            #...#E#
                            #.....#
                            #######
                        """.trimIndent()
                    }
                    (fightingArea[1][3] as Goblin).hitPoints `should equal` 200
                    (fightingArea[2][4] as Elf).hitPoints `should equal` 197
                    (fightingArea[2][5] as Goblin).hitPoints `should equal` 197
                    (fightingArea[3][3] as Goblin).hitPoints `should equal` 200
                    (fightingArea[3][5] as Goblin).hitPoints `should equal` 197
                    (fightingArea[4][5] as Elf).hitPoints `should equal` 197
                }
            }
        }
    }
})

typealias FightingArea = MutableList<MutableList<Field?>>
private fun FightingArea.move() {
    val creaturesInFightingOrder = getCreaturesInFightingOrder()
    creaturesInFightingOrder.forEach { creature ->
        val previousCoord = creature.coord
        creature.move(this)
        this[previousCoord.y][previousCoord.x] = null
        this[creature.coord.y][creature.coord.x] = creature
    }
}
private fun FightingArea.moveAndFight() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}


private fun FightingArea.print(overlay: Set<Coord>? = null, overlayChar: Char? = null) = this.mapIndexed { y, rows ->
    rows.mapIndexed { x, field ->
        if (overlay != null && Coord(x, y) in overlay) overlayChar
        else
            when(field) {
                is Goblin -> 'G'
                is Elf -> 'E'
                is Wall -> '#'
                null -> '.'
                else -> '!'
            }
    }.joinToString("")
}.joinToString("\n")

data class Coord(val x: Int, val y: Int)

fun FightingArea.getCreaturesInFightingOrder() = this.flatMap { rows ->
    rows.mapNotNull { field ->
        if (field is Creature) field
        else null
    }
}
fun parseFightingArea(input: String): FightingArea =
        input.split("\n").mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                when(c) {
                    'G' -> Goblin(x, y)
                    'E' -> Elf(x, y)
                    '#' -> Wall(x, y)
                    '.' -> null
                    else -> throw IllegalArgumentException("Illegal track element $c")
                }
            }.toMutableList()
        }.toMutableList()

val adjacentSquareOffsets = listOf( Coord(0, -1), Coord(-1, 0), Coord(1, 0), Coord(0, 1))
fun getAdjacentSquares(fightingArea: FightingArea, coord: Coord) = adjacentSquareOffsets.mapNotNull { offset ->
    val adjacentCoord = Coord(coord.x + offset.x, coord.y + offset.y)
    val field = fightingArea[adjacentCoord.y][adjacentCoord.x]
    if (field == null) adjacentCoord
    else null
}

fun isAdjacentSquares(coord1: Coord, coord2: Coord) = adjacentSquareOffsets.any { offset ->
    val adjacentCoord = Coord(coord1.x + offset.x, coord1.y + offset.y)
    coord2 == adjacentCoord
}

abstract class Field(open val coord: Coord) {
    fun getAdjacentSquares(fightingArea: FightingArea) = getAdjacentSquares(fightingArea, coord)
}

data class Wall(override val coord: Coord) : Field(coord) {
    constructor(x: Int, y: Int) : this(Coord(x, y))
}
sealed class Creature(override var coord: Coord, var hitPoints: Int = 200, val attackPower: Int = 3) : Field(coord) {

    fun getTargetCreatures(fightingArea: FightingArea): List<Creature> =
        fightingArea.flatMap { row ->
            row.filterIsInstance<Creature>()
                .filter { field -> field::class != this::class }
        }

    fun getAdjacentTargetCreatures(fightingArea: FightingArea) = getTargetCreatures(fightingArea)
            .filter { targetCreature -> isAdjacentSquares(targetCreature.coord, coord) }

    fun getTargetSquares(fightingArea: FightingArea) = getTargetCreatures(fightingArea).flatMap { target ->
        target.getAdjacentSquares(fightingArea)
    }.toSet()

    fun getNearestTargetSquarePath(fightingArea: FightingArea): List<Coord> {
        if (getAdjacentTargetCreatures(fightingArea).isNotEmpty()) return emptyList() // Already near target
        val targetSquares = getTargetSquares(fightingArea)
        val adjacentSquares = getAdjacentSquares(fightingArea)
        val start = adjacentSquares.map { listOf(it) }
        return getNearestTargetSquarePath(fightingArea, start, targetSquares, emptySet())
    }

    private tailrec fun getNearestTargetSquarePath(fightingArea: FightingArea, interimResults: List<List<Coord>>, targets: Set<Coord>, alreadyChecked: Set<Coord>): List<Coord> {
        if (interimResults.isEmpty()) return emptyList()
        interimResults.forEach {
            if (it.last() in targets) return it
        }
        val nextAlreadyChecked = alreadyChecked + interimResults.map { it.last() }
        val nextInterimResults = interimResults.flatMap { path ->
            val last = path.last()
            val adjacentSquares = getAdjacentSquares(fightingArea, last)
            adjacentSquares.filter { it !in alreadyChecked }.map { path + it}
        }
        return getNearestTargetSquarePath(fightingArea, nextInterimResults, targets, nextAlreadyChecked)
    }

    fun move(fightingArea: FightingArea) {
        val path = getNearestTargetSquarePath(fightingArea)
        if (path.isNotEmpty()) {
            val first = path.first()
            coord = Coord(first.x, first.y)
        }
    }

    fun fight(fightingArea: FightingArea) {
        val target = getAdjacentTargetCreatures(fightingArea).minBy { it.hitPoints }
        if (target != null) {
            target.hitPoints -= attackPower
            if (target.hitPoints < 0)
                fightingArea[target.coord.y][target.coord.x] = null // Killed creature should be removed from fighting area
        }
    }
}

data class Goblin(override var coord: Coord) : Creature(coord) {
    constructor(x: Int, y: Int) : this(Coord(x, y))
}
data class Elf(override var coord: Coord) : Creature(coord) {
    constructor(x: Int, y: Int) : this(Coord(x, y))
}

