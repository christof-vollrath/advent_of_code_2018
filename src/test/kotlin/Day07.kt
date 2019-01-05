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

--- Part Two ---

As you're about to begin construction, four of the Elves offer to help.
"The sun will set soon; it'll go faster if we work together."
Now, you need to account for multiple people working on steps simultaneously.
If multiple steps are available, workers should still begin them in alphabetical order.

Each step takes 60 seconds plus an amount corresponding to its letter: A=1, B=2, C=3, and so on.
So, step A takes 60+1=61 seconds, while step Z takes 60+26=86 seconds.
No time is required between steps.

To simplify things for the example, however, suppose you only have help from one Elf (a total of two workers)
and that each step takes 60 fewer seconds (so that step A takes 1 second and step Z takes 26 seconds).
Then, using the same instructions as above, this is how each second would be spent:

Second   Worker 1   Worker 2   Done
   0        C          .
   1        C          .
   2        C          .
   3        A          F       C
   4        B          F       CA
   5        B          F       CA
   6        D          F       CAB
   7        D          F       CAB
   8        D          F       CAB
   9        D          .       CABF
  10        E          .       CABFD
  11        E          .       CABFD
  12        E          .       CABFD
  13        E          .       CABFD
  14        E          .       CABFD
  15        .          .       CABFDE

Each row represents one second of time.
The Second column identifies how many seconds have passed as of the beginning of that second.
Each worker column shows the step that worker is currently doing (or . if they are idle).
The Done column shows completed steps.

Note that the order of the steps has changed;
this is because steps now take time to finish and multiple workers can begin multiple steps simultaneously.

In this example, it would take 15 seconds for two workers to complete these steps.

With 5 workers and the 60+ second step durations described above, how long will it take to complete all of the steps?


 */

class Instructions {
    val stepMap = mutableMapOf<Char, Step>()
    val start: List<Step>
        get() = (allOutGoing-allInGoing).mapNotNull { stepMap[it] }

    val allInGoing = mutableSetOf<Char>()
    val allOutGoing = mutableSetOf<Char>()

    fun add(from: Char, to: Char) {
        val fromStep = stepMap.getOrPut(from) { Step(from) }
        val toStep = stepMap.getOrPut(to) { Step(to) }
        fromStep.outGoing.add(toStep.id)
        toStep.inGoing.add(fromStep.id)
        allOutGoing.add(fromStep.id)
        allInGoing.add(toStep.id)
    }
}

data class Step(val id: Char, val outGoing: MutableSet<Char> = mutableSetOf()) {
    val inGoing: MutableSet<Char> = mutableSetOf()
}


fun traverseSteps(instructions: Instructions): List<Char> = traverseSteps(instructions, instructions.start, emptyList())

tailrec fun traverseSteps(instructions: Instructions, steps: List<Step>, alreadyVisited: List<Char>): List<Char> {
    val sorted = steps.sortedBy { it.id }
    val executableStep = sorted.firstOrNull { step ->
        step.inGoing.all { it in alreadyVisited }
    }
    val next = steps.flatMap { step ->
        if (step == executableStep)
            executableStep.outGoing.mapNotNull { instructions.stepMap[it] }
        else listOf(step)
    }.distinctBy { it.id }
    return if (executableStep == null) alreadyVisited
    else {
        traverseSteps(instructions, next, alreadyVisited + executableStep.id)
    }
}

fun parseInstructions(input: String): Instructions {
    val result = Instructions()
    input.split("\n").forEach { line ->
        val regex = """Step (\p{Lu}) must be finished before step (\p{Lu}) can begin.""".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Can not parse line=$line")
        if (match.groupValues.size != 3) throw IllegalArgumentException("Not all elements parsed")
        val values = match.groupValues
        result.add(values[1][0], values[2][0])
    }
    return result
}

data class Work(val step: Step, val time: Int, var progress: Int = 0)

fun completeSteps(instructions: Instructions, nrWorkers: Int, baseTime: Int): Int {
    fun calculateTime(step: Char) = baseTime + (step - 'A') + 1
    var currentWork = instructions.start.map { Work(it, calculateTime(it.id)) }
    var executing = emptyList<Work>()
    val alreadyExecuted = mutableListOf<Char>()
    var totalTime = 0
    while(true) {
        currentWork = currentWork.flatMap { work ->
            if (work.progress == work.time) { // completed
                alreadyExecuted.add(work.step.id)
                work.step.outGoing.mapNotNull { nextStep ->
                    if (currentWork.none { it.step.id == nextStep }) Work(instructions.stepMap[nextStep]!!, calculateTime(nextStep))
                    else null // already started
                }
            } else listOf(work)
        }
        if (currentWork.isEmpty()) return totalTime
        executing = executing.filter { it.time > it.progress } // completed
        if (executing.size < nrWorkers) {
            val executable = currentWork.filter { work ->
                work.step.inGoing.all { it in alreadyExecuted }
            }.sortedBy { it.step.id }
            executing += executable.filter { it !in executing }.take(nrWorkers - executing.size) // add new workers
        }
        executing.forEach { it.progress++ }
        totalTime++
    }
}

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
        given("example extended with one two starting nodes") {
            val input = """Step C must be finished before step A can begin.
                    Step C must be finished before step F can begin.
                    Step A must be finished before step B can begin.
                    Step A must be finished before step D can begin.
                    Step B must be finished before step E can begin.
                    Step D must be finished before step E can begin.
                    Step F must be finished before step E can begin.
                    Step X must be finished before step E can begin.
                """.trimIndent()

            it("should traverse steps correctly") {
                traverseSteps(parseInstructions(input)).joinToString("") `should equal` "CABDFXE"
            }
        }
        given("exercise") {
            val exerciseInput = readResource("day07Input.txt")
            val input = parseInstructions(exerciseInput)
            traverseSteps(input).joinToString("") `should equal` "BCADPVTJFZNRWXHEKSQLUYGMIO"
        }
    }
    describe("part 2") {
        given("example") {
            val input = """Step C must be finished before step A can begin.
                    Step C must be finished before step F can begin.
                    Step A must be finished before step B can begin.
                    Step A must be finished before step D can begin.
                    Step B must be finished before step E can begin.
                    Step D must be finished before step E can begin.
                    Step F must be finished before step E can begin.
                """.trimIndent()
            val instructions = parseInstructions(input)
            it("should take 60*6+21 seconds for a single worker") {
                completeSteps(instructions, 1, 60) `should equal` 381
            }
            it("should take 15 seconds for a two workers and zero base time") {
                completeSteps(instructions, 2, 0) `should equal` 15
            }
        }
        given("exercise") {
            val exerciseInput = readResource("day07Input.txt")
            val instructions = parseInstructions(exerciseInput)
            it("should find solution") {
                completeSteps(instructions, 5, 60) `should equal` 973
            }
        }
    }
})
