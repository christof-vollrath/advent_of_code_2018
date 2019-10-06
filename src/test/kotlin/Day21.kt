import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.amshove.kluent.`should equal`

/*
--- Day 21: Chronal Conversion ---

You should have been watching where you were going, because as you wander the new North Pole base,
you trip and fall into a very deep hole!

Just kidding. You're falling through time again.

If you keep up your current pace, you should have resolved all of the temporal anomalies
by the next time the device activates.
Since you have very little interest in browsing history in 500-year increments for the rest of your life,
you need to find a way to get back to your present time.

After a little research, you discover two important facts about the behavior of the device:

First, you discover that the device is hard-wired to always send you back in time in 500-year increments.
Changing this is probably not feasible.

Second, you discover the activation system (your puzzle input) for the time travel module.
Currently, it appears to run forever without halting.

If you can cause the activation system to halt at a specific moment,
maybe you can make the device send you so far back in time that you cause an integer underflow in time itself
and wrap around back to your current time!

The device executes the program as specified in manual section one (Day 16) and manual section two (Day 19).

Your goal is to figure out how the program works and cause it to halt.
You can only control register 0; every other register begins at 0 as usual.

Because time travel is a dangerous activity, the activation system begins with a few instructions which verify
that bitwise AND (via bani) does a numeric operation and not an operation as if the inputs were interpreted as strings.
If the test fails, it enters an infinite loop re-running the test instead of allowing the program to execute normally.
If the test passes, the program continues, and assumes that all other bitwise operations (banr, bori, and borr)
also interpret their inputs as numbers.
(Clearly, the Elves who wrote this system were worried that someone might introduce a bug
while trying to emulate this system with a scripting language.)

What is the lowest non-negative integer value for register 0 that causes the program
to halt after executing the fewest instructions?
(Executing the same instruction multiple times counts as multiple instructions executed.)

--- Part Two ---

In order to determine the timing window for your underflow exploit, you also need an upper bound:

What is the lowest non-negative integer value for register 0 that causes the program
to halt after executing the most instructions?
(The program must actually halt; running forever does not count as halting.)


 */

class Day21Spec : Spek({

    describe("part 1") {
        val inputString = readResource("day21Input.txt")
        val commandsWithDeclaration = parseCommandsWithDeclaration(inputString)
        it("should show what's happening after line 24 and what has to be written into register 0 so that eqrr in line 28 is true") {
            var executionCounter = 0
            val registers = listOf(30842, 0, 0, 0, 0, 0)
            val result = executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second, registers)  { cmd, registers, ip ->
                if (ip > 25) println("$executionCounter cmd=$cmd registers=$registers ip=$ip")
                executionCounter++
                false
            }
        }
    }
    describe("part 2") {
        val inputString = readResource("day21Input.txt")
        val commandsWithDeclaration = parseCommandsWithDeclaration(inputString)
        it("should show numbers in register 2 when comparing") {
            var executionCounter = 0L
            val registers = listOf(16777215, 0, 0, 0, 0, 0)
            val numberExecutionsMap = mutableMapOf<Int, Long>()
            executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second, registers)  { cmd, registers, ip ->
                val numberRepeated = if (ip == 28) {
                    val existingExecutions = numberExecutionsMap[registers[2]]
                    if (existingExecutions != null) {
                        println("Number ${registers[2]} repeated")
                        true
                    } else {
                        numberExecutionsMap[registers[2]] = executionCounter
                        false
                    }
                } else false
                executionCounter++
                numberRepeated
            }
            val result = numberExecutionsMap.entries.sortedByDescending { it.value }.first()
            result `should equal` 10748062
        }
    }

})

