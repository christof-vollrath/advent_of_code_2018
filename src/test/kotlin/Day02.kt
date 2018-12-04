
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*

--- Day 2: Inventory Management System ---
You stop falling through time, catch your breath, and check the screen on the device.
"Destination reached. Current Year: 1518. Current Location: North Pole Utility Closet 83N10."
You made it! Now, to find those anomalies.

Outside the utility closet, you hear footsteps and a voice. "...I'm not sure either.
But now that so many people have chimneys, maybe he could sneak in that way?"
Another voice responds, "Actually, we've been working on a new kind of suit
that would let him fit through tight spaces like that.
But, I heard that a few days ago, they lost the prototype fabric, the design plans, everything!
Nobody on the team can even seem to remember important details of the project!"

"Wouldn't they have had enough fabric to fill several boxes in the warehouse?
They'd be stored together, so the box IDs should be similar.
Too bad it would take forever to search the warehouse for two similar box IDs..."
They walk too far away to hear any more.

Late at night, you sneak to the warehouse - who knows what kinds of paradoxes you could cause if you were discovered -
and use your fancy wrist device to quickly scan every box and produce a list of the likely candidates
(your puzzle input).

To make sure you didn't miss any, you scan the likely candidate boxes again,
counting the number that have an ID containing exactly two of any letter and then separately
counting those with exactly three of any letter.
You can multiply those two counts together to get a rudimentary checksum and compare it to what your device predicts.

For example, if you see the following box IDs:

abcdef contains no letters that appear exactly two or three times.
bababc contains two a and three b, so it counts for both.
abbcde contains two b, but no letter appears exactly three times.
abcccd contains three c, but no letter appears exactly two times.
aabcdd contains two a and two d, but it only counts once.
abcdee contains two e.
ababab contains three a and three b, but it only counts once.

Of these box IDs, four of them contain a letter which appears exactly twice,
and three of them contain a letter which appears exactly three times.
Multiplying these together produces a checksum of 4 * 3 = 12.

What is the checksum for your list of box IDs?

--- Part Two ---

Confident that your list of box IDs is complete, you're ready to find the boxes full of prototype fabric.

The boxes will have IDs which differ by exactly one character at the same position in both strings. 
For example, given the following box IDs:

abcde
fghij
klmno
pqrst
fguij
axcye
wvxyz

The IDs abcde and axcye are close, but they differ by two characters (the second and fourth). 
However, the IDs fghij and fguij differ by exactly one character, the third (h and u). Those must be the correct boxes.

What letters are common between the two correct box IDs? 
(In the example above, this is found by removing the differing character from either ID, producing fgij.)


*/


fun hasRepeatedChars(input: String, nr: Int)  = input.groupBy { it }.values.any { it.size == nr }

fun countBoxIdsWithRepeatedChars(input: List<String>,  nr: Int) = input.filter { hasRepeatedChars(it, nr) }.count()

fun boxIdsChecksum(input: List<String>) = countBoxIdsWithRepeatedChars(input, 2) * countBoxIdsWithRepeatedChars(input, 3)

class Day2Spec : Spek({

    describe("part 1") {
        given("example input") {
            val input = listOf("abcdef", "bababc", "abbcde", "abcccd", "aabcdd", "abcdee", "ababab")

            it("should calculate checksum") {
                boxIdsChecksum(input) `shouldEqual` 12
            }
        }
        describe("hasRepeatedChars") {
            val testData = arrayOf(
                    data("abcdef",false, false),
                    data("bababc",true,  true),
                    data("abbcde",true,  false),
                    data("abcccd",false, true),
                    data("aabcdd",true,  false),
                    data("abcdee",true,  false),
                    data("ababab",false,  true)
            )

            onData("call for action %s", with = *testData) { input, expected2, expected3 ->
                val result2 = hasRepeatedChars(input, 2)
                val result3 = hasRepeatedChars(input, 3)
                it("returns $expected2 $expected3") {
                    result2 `should equal` expected2
                    result3 `should equal` expected3
                }
            }
        }

        describe("exercise") {
            given("exercise input") {
                val input = readResource("day02Input.txt").split('\n')
                it("should calculate correct result") {
                    val result = boxIdsChecksum(input)
                    result `should equal` 5727
                }

            }
        }
    }

    describe("part 2") {
        describe("similarIds") {
            val testData = arrayOf(
                    data("abcde", "abcde", false),
                    data("klmno", "fghij", false),
                    data("fghij", "fguij",  true)
            )

            onData("call for action %s", with = *testData) { input1, input2, expected ->
                val result = similarIds(input1, input2)
                it("returns $expected") {
                    result `should equal` expected
                }
            }
        }
        given("example input") {
            val input = listOf("abcde", "fghij", "klmno", "pqrst", "fguij", "axcye", "wvxyz")

            it("should find pair of similar ids") {
                findSimilarPair(input) `shouldEqual` Pair("fghij", "fguij")
            }

            it("should find common letters in pair of similar ids") {
                findCommonLetters(input) `shouldEqual` "fgij"
            }
        }
        describe("exercise") {
            given("exercise input") {
                val input = readResource("day02Input.txt").split('\n')
                it("should calculate correct result") {
                    val result = findCommonLetters(input)
                    result `should equal` "uwfmdjxyxlbgnrotcfpvswaqh"
                }

            }
        }
    }

})

fun findSimilarPair(input: List<String>): Pair<String, String> = input.flatMap { boxId1 ->
        input.mapNotNull { boxId2 ->
            if (similarIds(boxId1, boxId2)) boxId1 to boxId2
            else null
        }
    }
    .first()

fun similarIds(boxId1: String, boxId2: String)= boxId1.zip(boxId2)
        .filter { (c1, c2) -> c1 != c2}
        .count() == 1

fun findCommonLetters(input: List<String>) = findSimilarPair(input).let { (boxId1, boxId2) ->
    boxId1.zip(boxId2).mapNotNull { (c1, c2) ->
        if (c1 == c2) c1
        else null
    }
    .joinToString("")
}
