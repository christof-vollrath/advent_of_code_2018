import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 7: The Sum of Its Parts ---

You find yourself standing on a snow-covered coastline; apparently, you landed a little off course.
The region is too hilly to see the North Pole from here,
but you do spot some Elves that seem to be trying to unpack something that washed ashore.
 It's quite cold out, so you decide to risk creating a paradox by asking them for directions.

"Oh, are you the search party?" Somehow, you can understand whatever Elves from the year 1018 speak;
you assume it's Ancient Nordic Elvish. Could the device on your wrist also be a translator?
"Those clothes don't look very warm; take this." They hand you a heavy coat.

"We do need to find our way back to the North Pole, but we have higher priorities at the moment.
You see, believe it or not, this box contains something that will solve all of Santa's transportation problems -
at least, that's what it looks like from the pictures in the instructions."
It doesn't seem like they can read whatever language it's in, but you can: "Sleigh kit. Some assembly required."

"'Sleigh'? What a wonderful name! You must help us assemble this 'sleigh' at once!"
They start excitedly pulling more parts out of the box.

The instructions specify a series of steps and requirements about which steps must be finished before others can begin
(your puzzle input). Each step is designated by a single letter.

For example, suppose you have the following instructions:

Step C must be finished before step A can begin.
Step C must be finished before step F can begin.
Step A must be finished before step B can begin.
Step A must be finished before step D can begin.
Step B must be finished before step E can begin.
Step D must be finished before step E can begin.
Step F must be finished before step E can begin.

Visually, these requirements look like this:


  -->A--->B--
 /    \      \
C      -->D----->E
 \           /
  ---->F-----

Your first goal is to determine the order in which the steps should be completed.
If more than one step is ready, choose the step which is first alphabetically.
In this example, the steps would be completed as follows:

Only C is available, and so it is done first.
Next, both A and F are available. A is first alphabetically, so it is done next.
Then, even though F was available earlier, steps B and D are now also available,
and B is the first alphabetically of the three.
After that, only D and F are available. E is not available because only some of its prerequisites are complete.
Therefore, D is completed next.
F is the only choice, so it is done next.
Finally, E is completed.
So, in this example, the correct order is CABDFE.

In what order should the steps in your instructions be completed?

 */

class Day07Spec : Spek({

    describe("part 1") {
        given("example") {
            val input = """Step C must be finished before step A can begin.
                    Step C must be finished before step F can begin.
                    Step A must be finished before step B can begin.
                    Step A must be finished before step D can begin.
                    Step B must be finished before step E can begin.
                    Step D must be finished before step E can begin.
                    Step F must be finished before step E can begin.
                """.trimIndent()

            it("should parse input to graph") {
                val a = Step('A', mutableSetOf('B', 'D'))
                val b = Step('B', mutableSetOf('E'))
                val c = Step('C', mutableSetOf('A', 'F'))
                val d = Step('D', mutableSetOf('E'))
                val e = Step('E', mutableSetOf())
                val f = Step('F', mutableSetOf('E'))

                val instructions = parseInstructions(input)
                instructions.start `should equal` listOf(c)
                instructions.stepMap `should equal` mapOf (
                            'A' to a,
                            'B' to b,
                            'C' to c,
                            'D' to d,
                            'E' to e,
                            'F' to f
                        )
                instructions.allOutGoing-instructions.allInGoing `should equal` setOf('C')
            }
            it("should traverse steps correctly") {
                traverseSteps(parseInstructions(input)).joinToString("") `should equal` "CABDFE"
            }
        }
        given("exercise") {
            val exerciseInput = readResource("day07Input.txt")
            val input = parseInstructions(exerciseInput)
            println("allInGoing=${input.allInGoing} allOutGoing=${input.allOutGoing} allOutGoing-allInGoing=${input.allOutGoing-input.allInGoing}")
            traverseSteps(input).joinToString("") `should equal` "CABDFE"
        }
    }
})

fun traverseSteps(instructions: Instructions): List<Char> {
    val alreadyVisited = mutableSetOf<Char>()
    return instructions.start.flatMap {
        traverseSteps(instructions, it, alreadyVisited)
    }
}

fun traverseSteps(instructions: Instructions, step: Step, alreadyVisited: MutableSet<Char>): List<Char> {
    println("traverseSteps id=${step.id} alreadyVisited=${alreadyVisited}")
    if (step.id in alreadyVisited) return emptyList()
    if (! step.inGoing.all { it in alreadyVisited}) return emptyList() // Not yet executable
    val sortedOutgoing = step.outGoing.sortedBy { it }
    alreadyVisited.add(step.id)
    return listOf(step.id) + sortedOutgoing.flatMap { stepId ->
        traverseSteps(instructions, instructions.stepMap[stepId]!!, alreadyVisited)
    }
}

fun parseInstructions(input: String): Instructions {

    val result = Instructions()
    input.split("\n").forEach { line ->
        val  regex = """Step (\p{Lu}) must be finished before step (\p{Lu}) can begin.""".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Can not parse line=$line")
        if (match.groupValues.size != 3) throw IllegalArgumentException("Not all elements parsed")
        val values = match.groupValues
        result.add(values[1][0], values[2][0])
    }
    return result
}

class Instructions {
    val stepMap = mutableMapOf<Char, Step>()
    val start: List<Step>
        get() = (allOutGoing-allInGoing).mapNotNull { stepMap[it] }.sortedBy { it.id }

    val allInGoing = mutableSetOf<Char>()
    val allOutGoing = mutableSetOf<Char>()

    private fun getOrCreate(map: MutableMap<Char, Step>, c: Char) = map[c]?: run {
        val newStep = Step(c)
        map[c] = newStep
        newStep
    }

    fun add(from: Char, to: Char) {
        val fromStep = getOrCreate(stepMap, from)
        val toStep = getOrCreate(stepMap, to)
        fromStep.outGoing.add(toStep.id)
        toStep.inGoing.add(fromStep.id)
        allOutGoing.add(fromStep.id)
        allInGoing.add(toStep.id)
    }
}

data class Step(val id: Char, val outGoing: MutableSet<Char> = mutableSetOf()) {
    val inGoing: MutableSet<Char> = mutableSetOf()
}

