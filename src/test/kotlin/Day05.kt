import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 5: Alchemical Reduction ---

You've managed to sneak in to the prototype suit manufacturing lab.
The Elves are making decent progress, but are still struggling with the suit's size reduction capabilities.

While the very latest in 1518 alchemical technology might have solved their problem eventually, you can do better.
You scan the chemical composition of the suit's material and discover that it is formed by extremely long polymers
(one of which is available as your puzzle input).

The polymer is formed by smaller units which, when triggered,
react with each other such that two adjacent units of the same type and opposite polarity are destroyed.
Units' types are represented by letters; units' polarity is represented by capitalization.
For instance, r and R are units with the same type but opposite polarity,
whereas r and s are entirely different types and do not react.

For example:

In aA, a and A react, leaving nothing behind.
In abBA, bB destroys itself, leaving aA. As above, this then destroys itself, leaving nothing.
In abAB, no two adjacent units are of the same type, and so nothing happens.
In aabAAB, even though aa and AA are of the same type, their polarities match, and so nothing happens.
Now, consider a larger example, dabAcCaCBAcCcaDA:

dabAcCaCBAcCcaDA  The first 'cC' is removed.
dabAaCBAcCcaDA    This creates 'Aa', which is removed.
dabCBAcCcaDA      Either 'cC' or 'Cc' are removed (the result is the same).
dabCBAcaDA        No further actions can be taken.

After all possible reactions, the resulting polymer contains 10 units.

How many units remain after fully reacting the polymer you scanned?

--- Part Two ---

Time to improve the polymer.

One of the unit types is causing problems; it's preventing the polymer from collapsing as much as it should.
Your goal is to figure out which unit type is causing the most problems,
remove all instances of it (regardless of polarity),
fully react the remaining polymer, and measure its length.

For example, again using the polymer dabAcCaCBAcCcaDA from above:

Removing all A/a units produces dbcCCBcCcD. Fully reacting this polymer produces dbCBcD, which has length 6.
Removing all B/b units produces daAcCaCAcCcaDA. Fully reacting this polymer produces daCAcaDA, which has length 8.
Removing all C/c units produces dabAaBAaDA. Fully reacting this polymer produces daDA, which has length 4.
Removing all D/d units produces abAcCaCBAcCcaA. Fully reacting this polymer produces abCBAc, which has length 6.

In this example, removing all C/c units was best, producing the answer 4.

What is the length of the shortest polymer you can produce by removing all units
of exactly one type and fully reacting the result?

 */

fun singleReaction(s: String): String {
    var reacted = false
    val result = s.zipWithNext().mapNotNull { (c1, c2) ->
        if (reacted) {
            reacted = false; null
        } else if (c1.toLowerCase() == c2.toLowerCase() && c1 != c2) {
            reacted = true; null
        } // react and destroy
        else c1
    }
    return (if (!reacted) result + s.last()
    else result).joinToString("")
}

tailrec fun repeatReactions(s: String): String {
    val result = singleReaction(s)
    return if (result.length == s.length) result
    else repeatReactions(result)
}

fun findBestCorrection(s: String): Pair<Char, String> {
    val letters = getAllLetters(s)
    val allCorrections = letters.map {
        it to repeatReactions(removeLetter(s, it))
    }
    return allCorrections.minBy { (_, s) ->
        s.length
    }!!
}

fun removeLetter(s: String, c: Char) = s.filter { it.toLowerCase() != c}

fun getAllLetters(s: String) = s.map { it.toLowerCase() }.toSet().toList().sorted()

class Day05Spec : Spek({

    val exerciseInput = readResource("day05Input.txt")
    describe("part 1") {
        given("example cases") {
            describe("singleReaction") {
                val testData = arrayOf(
                        data("aA", ""),
                        data("abBA", "aA"),
                        data("abAB", "abAB"),
                        data("aabAAB", "aabAAB")
                )

                onData("singleReaction %s", with = *testData) { input, expected ->
                    val result = singleReaction(input)
                    it("returns $expected") {
                        result `should equal` expected
                    }
                }
            }
        }

        given("larger example") {
            describe("repeatReactions") {
                it("should execute all reactions") {
                    val result = repeatReactions("dabAcCaCBAcCcaDA")
                    result `should equal` "dabCBAcaDA"
                    result.length `should equal` 10
                }
            }
        }
        it("should solve part 1") {
            val result = repeatReactions(exerciseInput)
            result.length `should equal` 10804
        }

    }
    describe("part 2") {
        given("larger example part 2") {
            val example = "dabAcCaCBAcCcaDA"
            it("getAllLetters") {
                getAllLetters(example) `should equal` "abcd".toList()
            }
            it("removeLetter") {
                removeLetter(example, 'a') `should equal` "dbcCCBcCcD"
            }
            it("findBestCorrection") {
                val result = findBestCorrection(example)
                result `should equal` ('c' to "daDA")
                result.second.length `should equal` 4
            }
        }
        it("should solve part 2") {
            val result = findBestCorrection(exerciseInput).second
            result.length `should equal` 6650
        }
    }
})
