import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit

/*
--- Day 14: Chocolate Charts ---

You finally have a chance to look at all of the produce moving around.
Chocolate, cinnamon, mint, chili peppers, nutmeg, vanilla... the Elves must be growing these plants
to make hot chocolate!
As you realize this, you hear a conversation in the distance.
When you go to investigate, you discover two Elves in what appears to be a makeshift underground kitchen/laboratory.

The Elves are trying to come up with the ultimate hot chocolate recipe;
 they're even maintaining a scoreboard which tracks the quality score (0-9) of each recipe.

Only two recipes are on the board: the first recipe got a score of 3, the second, 7.
Each of the two Elves has a current recipe:
the first Elf starts with the first recipe, and the second Elf starts with the second recipe.

To create new recipes, the two Elves combine their current recipes.
This creates new recipes from the digits of the sum of the current recipes' scores.
With the current recipes' scores of 3 and 7, their sum is 10, and so two new recipes would be created:
the first with score 1 and the second with score 0. If the current recipes' scores were 2 and 3, the sum, 5,
would only create one recipe (with a score of 5) with its single digit.

The new recipes are added to the end of the scoreboard in the order they are created.
So, after the first round, the scoreboard is 3, 7, 1, 0.

After all new recipes are added to the scoreboard, each Elf picks a new current recipe.
To do this, the Elf steps forward through the scoreboard a number of recipes equal to 1 plus the score
of their current recipe.
So, after the first round, the first Elf moves forward 1 + 3 = 4 times,
while the second Elf moves forward 1 + 7 = 8 times.
If they run out of recipes, they loop back around to the beginning.
After the first round, both Elves happen to loop around until they land on the same recipe
that they had in the beginning; in general, they will move to different recipes.

Drawing the first Elf as parentheses and the second Elf as square brackets, they continue this process:

(3)[7]
(3)[7] 1  0
 3  7  1 [0](1) 0
 3  7  1  0 [1] 0 (1)
(3) 7  1  0  1  0 [1] 2
 3  7  1  0 (1) 0  1  2 [4]
 3  7  1 [0] 1  0 (1) 2  4  5
 3  7  1  0 [1] 0  1  2 (4) 5  1
 3 (7) 1  0  1  0 [1] 2  4  5  1  5
 3  7  1  0  1  0  1  2 [4](5) 1  5  8
 3 (7) 1  0  1  0  1  2  4  5  1  5  8 [9]
 3  7  1  0  1  0  1 [2] 4 (5) 1  5  8  9  1  6
 3  7  1  0  1  0  1  2  4  5 [1] 5  8  9  1 (6) 7
 3  7  1  0 (1) 0  1  2  4  5  1  5 [8] 9  1  6  7  7
 3  7 [1] 0  1  0 (1) 2  4  5  1  5  8  9  1  6  7  7  9
 3  7  1  0 [1] 0  1  2 (4) 5  1  5  8  9  1  6  7  7  9  2

The Elves think their skill will improve after making a few recipes (your puzzle input).
However, that could take ages;
you can speed this up considerably by identifying the scores of the ten recipes after that.

For example:

If the Elves think their skill will improve after making 9 recipes,
the scores of the ten recipes after the first nine on the scoreboard would be 5158916779
(highlighted in the last line of the diagram).
After 5 recipes, the scores of the next ten would be 0124515891.
After 18 recipes, the scores of the next ten would be 9251071085.
After 2018 recipes, the scores of the next ten would be 5941429882.

What are the scores of the ten recipes immediately after the number of recipes in your puzzle input?

Your puzzle input is 890691.

--- Part Two ---

As it turns out, you got the Elves' plan backwards.
They actually want to know how many recipes appear on the scoreboard to the left of the first recipes
whose scores are the digits from your puzzle input.

51589 first appears after 9 recipes.
01245 first appears after 5 recipes.
92510 first appears after 18 recipes.
59414 first appears after 2018 recipes.

How many recipes appear on the scoreboard to the left of the score sequence in your puzzle input?

 */

data class RecipeList(var elements: List<Int>) {
    constructor(input: Int) : this(intToIntList(input))

    fun append(additionalElements: List<Int>) {
        elements += additionalElements
    }
    fun append(additionalElementsInt: Int) {
        append(intToIntList(additionalElementsInt))
    }
    fun cook(elve1: KitchenElve, elve2: KitchenElve) {
        val score1 = elements[elve1.pos]
        val score2 = elements[elve2.pos]
        append(score1 + score2)
        elve1.stepForward(1 + score1)
        elve2.stepForward(1 + score2)
    }

    fun cookUntil(elve1: KitchenElve, elve2: KitchenElve, nr: Int) {
        while(elements.size < nr) {
            cook(elve1, elve2)
            //if (elements.size % 100 == 0) println(elements.size)
        }
    }

    fun cookUntilPattern(elve1: KitchenElve, elve2: KitchenElve, pattern: String) {
        fun patternFound(elementsSizeBeforeCooking: Int): Boolean {
            val ignoreElements = elementsSizeBeforeCooking - pattern.length
            val checkedIgnoreElements = if (ignoreElements < 0) 0 else ignoreElements
            return elements.drop(checkedIgnoreElements).joinToString("").indexOf(pattern) >= 0
        }
        var elementsSizeBeforeCooking = elements.size
        while(! patternFound(elementsSizeBeforeCooking)) {
            elementsSizeBeforeCooking = elements.size
            cook(elve1, elve2)
            if (elements.size % 100 == 0) println("size=${elements.size} elve1=${elve1.pos} elve2=${elve2.pos}")
        }
    }
}

data class KitchenElve(var pos: Int, val recipeList: RecipeList) {
    fun stepForward(nr: Int) {
        pos = (pos + nr) % recipeList.elements.size
    }
}

private fun intToIntList(input: Int): List<Int> =
        if (input >= 10) intToIntList(input / 10) + listOf(input % 10)
        else listOf(input % 10)

class Day14Spec : Spek({

    describe("part 1") {
        describe("create recipe list") {
            given("a number") {
                val input = 37
                it("should create recipe list") {
                    val recipeList = RecipeList(input)
                    recipeList.elements `should equal` listOf(3, 7)
                }
            }
        }
        describe("append to recipe list") {
            given("a number") {
                val recipeList = RecipeList(37)
                val input = 10
                it("should append to recipe list") {
                    recipeList.append(input)
                    recipeList.elements `should equal` listOf(3, 7, 1, 0)
                }
            }
        }
        describe("cook") {
            given("a list of recipes and two elves") {
                val recipeList = RecipeList(37)
                val elve1 = KitchenElve(0, recipeList)
                val elve2 = KitchenElve(1, recipeList)
                it("should cook") {
                    recipeList.cook(elve1, elve2)
                    recipeList.elements `should equal` listOf(3, 7, 1, 0)
                    elve1.pos `should equal` 0
                    elve2.pos `should equal` 1
                }
                it("should cook more") {
                    repeat(5) { recipeList.cook(elve1, elve2) }
                    recipeList.elements `should equal` listOf(3, 7, 1, 0, 1, 0, 1, 2, 4, 5)
                    elve1.pos `should equal` 6
                    elve2.pos `should equal` 3
                }
            }
        }
        describe("cook several recipes") {
            given("a list of recipes and two elves") {
                val recipeList = RecipeList(37)
                val elve1 = KitchenElve(0, recipeList)
                val elve2 = KitchenElve(1, recipeList)
                it("should cook until 15 recipes are tested") {
                    recipeList.cookUntil(elve1, elve2, 15)
                    recipeList.elements.drop(5).take(10).joinToString("") `should equal` "0124515891"
                }
                it("should cook until 2028 recipes are tested") {
                    recipeList.cookUntil(elve1, elve2, 2028)
                    recipeList.elements.drop(2018).take(10).joinToString("") `should equal` "5941429882"
                }
            }
        }
        describe("exercise") {
            given("a list of recipes, two elves and the input number") {
                val input = 890691
                val recipeList = RecipeList(37)
                val elve1 = KitchenElve(0, recipeList)
                val elve2 = KitchenElve(1, recipeList)
                xit("should cook all recipes are tested") {
                    recipeList.cookUntil(elve1, elve2, input+10)
                    recipeList.elements.drop(input).take(10).joinToString("") `should equal` "8176111038"
                }
            }
        }
    }
    describe("part 2") {
        describe("example") {
            given("a list of recipes and two elves") {
                val recipeList = RecipeList(37)
                val elve1 = KitchenElve(0, recipeList)
                val elve2 = KitchenElve(1, recipeList)
                it("should cook until 51589 is found") {
                    val pattern = "51589"
                    recipeList.cookUntilPattern(elve1, elve2, pattern)
                    recipeList.elements.joinToString("").indexOf(pattern) `should equal` 9
                }
                it("should cook until 59414 is found") {
                    val pattern = "59414"
                    recipeList.cookUntilPattern(elve1, elve2, pattern)
                    recipeList.elements.joinToString("").indexOf(pattern) `should equal` 2018
                }
            }
        }
        describe("exercise") {
            given("a list of recipes, two elves and the input number") {
                val input = "890691"
                val recipeList = RecipeList(37)
                val elve1 = KitchenElve(0, recipeList)
                val elve2 = KitchenElve(1, recipeList)
                it("should cook until 890691 is found") {
                    recipeList.cookUntilPattern(elve1, elve2, input)
                    recipeList.elements.joinToString("").indexOf(input) `should equal` 2018
                }
            }
        }
    }
})
