import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 19: Go With The Flow ---

With the Elves well on their way constructing the North Pole base,
you turn your attention back to understanding the inner workings of programming the device.

You can't help but notice that the device's opcodes don't contain any flow control like jump instructions.
The device's manual goes on to explain:

"In programs where flow control is required,
the instruction pointer can be bound to a register so that it can be manipulated directly.
This way, setr/seti can function as absolute jumps, addr/addi can function as relative jumps,
and other opcodes can cause truly fascinating effects."

This mechanism is achieved through a declaration like #ip 1,
which would modify register 1 so that accesses to it let the program indirectly access the instruction pointer itself.
To compensate for this kind of binding, there are now six registers (numbered 0 through 5);
the five not bound to the instruction pointer behave as normal.
Otherwise, the same rules apply as the last time you worked with this device.

When the instruction pointer is bound to a register,
its value is written to that register just before each instruction is executed,
and the value of that register is written back to the instruction pointer
immediately after each instruction finishes execution.
Afterward, move to the next instruction by adding one to the instruction pointer,
even if the value in the instruction pointer was just updated by an instruction.
(Because of this, instructions must effectively set the instruction pointer to the instruction
before the one they want executed next.)

The instruction pointer is 0 during the first instruction, 1 during the second, and so on.
If the instruction pointer ever causes the device to attempt to load an instruction
outside the instructions defined in the program, the program instead immediately halts.
 The instruction pointer starts at 0.

It turns out that this new information is already proving useful:
the CPU in the device is not very powerful, and a background process is occupying most of its time.
You dump the background process' declarations and instructions to a file (your puzzle input),
making sure to use the names of the opcodes rather than the numbers.

For example, suppose you have the following program:

#ip 0
seti 5 0 1
seti 6 0 2
addi 0 1 0
addr 1 2 3
setr 1 0 0
seti 8 0 4
seti 9 0 5

When executed, the following instructions are executed.
Each line contains the value of the instruction pointer at the time the instruction started,
the values of the six registers before executing the instructions (in square brackets),
the instruction itself, and the values of the six registers after executing the instruction (also in square brackets).

ip=0 [0, 0, 0, 0, 0, 0] seti 5 0 1 [0, 5, 0, 0, 0, 0]
ip=1 [1, 5, 0, 0, 0, 0] seti 6 0 2 [1, 5, 6, 0, 0, 0]
ip=2 [2, 5, 6, 0, 0, 0] addi 0 1 0 [3, 5, 6, 0, 0, 0]
ip=4 [4, 5, 6, 0, 0, 0] setr 1 0 0 [5, 5, 6, 0, 0, 0]
ip=6 [6, 5, 6, 0, 0, 0] seti 9 0 5 [6, 5, 6, 0, 0, 9]

In detail, when running this program, the following events occur:

The first line (#ip 0) indicates that the instruction pointer should be bound to register 0 in this program.
This is not an instruction, and so the value of the instruction pointer does not change during the processing of this line.
The instruction pointer contains 0, and so the first instruction is executed (seti 5 0 1).
It updates register 0 to the current instruction pointer value (0), sets register 1 to 5,
sets the instruction pointer to the value of register 0 (which has no effect, as the instruction did not modify register 0),
and then adds one to the instruction pointer.

The instruction pointer contains 1, and so the second instruction, seti 6 0 2, is executed.
This is very similar to the instruction before it: 6 is stored in register 2,
and the instruction pointer is left with the value 2.

The instruction pointer is 2, which points at the instruction addi 0 1 0. This is like a relative jump:
the value of the instruction pointer, 2, is loaded into register 0.
Then, addi finds the result of adding the value in register 0 and the value 1, storing the result, 3, back in register 0.
Register 0 is then copied back to the instruction pointer, which will cause it to end up 1 larger
than it would have otherwise and skip the next instruction (addr 1 2 3) entirely.
Finally, 1 is added to the instruction pointer.
The instruction pointer is 4, so the instruction setr 1 0 0 is run.
This is like an absolute jump: it copies the value contained in register 1, 5, into register 0,
which causes it to end up in the instruction pointer. The instruction pointer is then incremented, leaving it at 6.
The instruction pointer is 6, so the instruction seti 9 0 5 stores 9 into register 5.
The instruction pointer is incremented, causing it to point outside the program, and so the program ends.

What value is left in register 0 when the background process halts?

--- Part Two ---

A new background process immediately spins up in its place.
It appears identical, but on closer inspection, you notice that this time,
register 0 started with the value 1.

What value is left in register 0 when this new background process halts?

 */

data class IpBinding(val ipRegister: Int)

fun parseCommandsWithDeclaration(input: String): Pair<IpBinding, List<Command>> {
    val lines = input.split("\n")
    val firstLine = lines[0]
    val otherLines = lines.drop(1)
    val ipBinding = with(firstLine) {
        val regex = """#ip (\d+)""".toRegex()
        val match = regex.find(this) ?: throw IllegalArgumentException("Can not parse input $firstLine in line 1")
        if (match.groupValues.size != 2) throw IllegalArgumentException("Only ${match.groupValues.size} elements parsed $firstLine in line 1")
        val values = match.groupValues
        IpBinding(values[1].toInt())
    }
    val regex = """([a-z]+) (\d+) (\d+) (\d+)""".toRegex()
    val commands = otherLines.mapIndexed { lineNr, line ->
        val match = regex.find(line) ?: throw IllegalArgumentException("Can not parse input $line in line ${lineNr+2}")
        if (match.groupValues.size != 5) throw IllegalArgumentException("Only ${match.groupValues.size} elements parsed $line in line ${lineNr+2}")
        val values = match.groupValues
        val commandName = values[1].mapIndexed { i, c -> if (i == 0) c.toUpperCase() else c }.joinToString("")
        val pars = values.drop(2).map { it.toInt() }
        val (a, b, c) = pars
        Class.forName(commandName).getConstructor(Int::class.java, Int::class.java, Int::class.java).newInstance(a, b, c) as Command
    }
    return ipBinding to commands
}
typealias ExecutionHook = (Command, List<Int>, Int) -> Boolean
fun executeCommands(ipBinding: IpBinding, commands: List<Command>, startRegisters: List<Int> = List(6) { 0 },
                    preExecutionHook: ExecutionHook? = null): List<Int> {
    var registers =  startRegisters
    var ip = 0
    while(ip >= 0 && ip < commands.size) {
        val command = commands[ip]
        if (preExecutionHook != null) {
            val breakExecution = preExecutionHook(command, registers, ip)
            if (breakExecution) break
        }
        val (nextIp, nextRegisters) = executeCommand(ip, ipBinding, registers, command)
        ip = nextIp; registers = nextRegisters
    }
    return registers
}

private fun executeCommand(ip: Int, ipBinding: IpBinding, registers: List<Int>, command: Command): Pair<Int, List<Int>> {
    val inputRegisters = registers as MutableList
    inputRegisters[ipBinding.ipRegister] = ip
    val changedRegisters = command.execute(inputRegisters)
    val nextIp = changedRegisters[ipBinding.ipRegister] + 1
    return nextIp to changedRegisters
}

/*
#ip 3
00 addi 0 16 IP  +
01 seti 1 - R5   |         < <  R5 = 1
02 seti 1 - R2   |       < | |
03 mulr R5 R2 R1 |     < | | |
04 eqrr R1 R4 R1 |     | | | |  if (R1 == R4)
05 addr R1 IP IP | +   | | | |
06 addi 6 1 IP   | --+ | | | |
07 addr R5 R0 R0 | < | | | | |  R0 += R5
08 addi R2 1 R2  | < - | | | |  R2++
09 gtrr R2 R4 R1 |     | | | |  if (R2 > R4)
10 addr 10 R1 IP | +   | | | |
11 seti 2 - IP   | ----+ | | |
12 addi R5 1 R5  | <     | | |  R5++
13 gtrr R5 R4 R1 |       | | |  if (R5 > R4)
14 addr R1 14 IP | +     | | |  |
15 seti 1 - IP   |   ----+ | |  |
16 mulr 16 16 IP |         | |  - Stop
17 addi R4 2 R4  <         | |  R4 += 2
18 mulr R4 R4 R4           | |  R4 = R4 ** 2
19 mulr 19 R4 R4           | |  R4 *= 19
20 muli R4 11 R4           | |  R4 *= 11
21 addi R1 6 R1            | |  R1 += 6
22 mulr R1 22 R1           | |  R1 *= 22
23 addi R1 21 R1           | |  R1 += 21
24 addr R4 R1 R4           | |  R4 += R1
25 addr 25 R0 IP +         | |  if (R0 > 0)
26 seti 0 - IP   ----------+ |
27 setr 27 - R1  <           |  -
28 mulr R1 28 R1             |  R1 *= 28
29 addr 29 R1 R1             |  R1 += 29
30 mulr 30 R1 R1             |  R1 *= 30
31 muli R1 14 R1             |  R1 *= 14
32 mulr R1 32 R1             |  R1 *= 32
33 addr R4 R1 R4             |  R4 += R1
34 seti 0 - R0               |  R0 = 0
35 seti 0 - IP   ------------+
 */

fun decompiledMainLoop(r4: Int): Int { // Lines 01 - 16
    var r0 = 0 // 34
    var r5 = 1 // 01
    do {
        var r2 = 1 // 02
        do {
            val r1 = r5 * r2 // 03
            if (r1 == r4) { // 04, 05, 06 r1 is divisor of r4
                r0 += r5 // 07
            }
            r2++ // 08
        } while (!(r2 > r4)) // 09, 10, 11
        r5++ // 12
    } while (!(r5 > r4)) // 13, 14, 15
    return r0
}

fun divisors(n: Int) = (1..n).filter {n % it == 0 }

class Day19Spec : Spek({

    describe("part 1") {
        describe("parse commands with declaration") {
            given("commands") {
                val input = """
                    #ip 0
                    seti 5 0 1
                    seti 6 0 2
                """.trimIndent()
                it("should parse correctly") {
                    parseCommandsWithDeclaration(input) `should equal` Pair(IpBinding(0),
                            listOf(Seti(5, 0, 1), Seti(6, 0, 2)))
                }
            }
        }
        describe("execute commands") {
            given("given one command with declaration") {
                val input = """
                    #ip 0
                    seti 5 0 1
                """.trimIndent()
                it("should execute command correctly") {
                    val commandsWithDeclaration = parseCommandsWithDeclaration(input)
                    executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second) `should equal` listOf(0, 5, 0, 0, 0, 0)
                }
            }
            given("given example commands with declaration") {
                val input = """
                    #ip 0
                    seti 5 0 1
                    seti 6 0 2
                    addi 0 1 0
                    addr 1 2 3
                    setr 1 0 0
                    seti 8 0 4
                    seti 9 0 5
                """.trimIndent()
                val commandsWithDeclaration = parseCommandsWithDeclaration(input)
                it("should execute commands correctly") {
                    val result = executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second)
                    result `should equal` listOf(6, 5, 6, 0, 0, 9)
                }
                it("should have the correct value in register 0") {
                    val result = executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second)
                    result[0] `should equal` 6
                }
            }
        }
        describe("find divisors") {
            it("should calculate divisors for 989") {
                divisors(989) `should equal` listOf(1, 23, 43, 989)
            }
        }
        describe("exercise") {
            given("exercise input") {
                val inputString = readResource("day19Input.txt")
                val commandsWithDeclaration = parseCommandsWithDeclaration(inputString)
                it("should execute commands correctly") {
                    val result = executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second)
                    result[0] `should equal` 1056
                }
                it("should execute some commands with trace output") {
                    var executionCounter = 0
                    var trace = ""
                    executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second) { cmd, registers, ip ->
                        trace += "cmd=$cmd registers=$registers ip=$ip\n"
                        executionCounter++
                        executionCounter >= 10
                    }
                    trace `should equal` """
                        cmd=Addi(a=3, b=16, c=3) registers=[0, 0, 0, 0, 0, 0] ip=0
                        cmd=Addi(a=4, b=2, c=4) registers=[0, 0, 0, 16, 0, 0] ip=17
                        cmd=Mulr(a=4, b=4, c=4) registers=[0, 0, 0, 17, 2, 0] ip=18
                        cmd=Mulr(a=3, b=4, c=4) registers=[0, 0, 0, 18, 4, 0] ip=19
                        cmd=Muli(a=4, b=11, c=4) registers=[0, 0, 0, 19, 76, 0] ip=20
                        cmd=Addi(a=1, b=6, c=1) registers=[0, 0, 0, 20, 836, 0] ip=21
                        cmd=Mulr(a=1, b=3, c=1) registers=[0, 6, 0, 21, 836, 0] ip=22
                        cmd=Addi(a=1, b=21, c=1) registers=[0, 132, 0, 22, 836, 0] ip=23
                        cmd=Addr(a=4, b=1, c=4) registers=[0, 153, 0, 23, 836, 0] ip=24
                        cmd=Addr(a=3, b=0, c=3) registers=[0, 153, 0, 24, 989, 0] ip=25
                        
                    """.trimIndent()
                }
                it("should calculate the value of R4 before entering the main loop in line 01") {
                    var r4 = 0
                    executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second) { _, registers, ip ->
                        val enteringLoop = ip == 1
                        if (enteringLoop) {
                            r4 = registers[4]
                        }
                        enteringLoop
                    }
                    r4 `should equal` 989
                }
                it("should calculate same value using decompiled function starting with 989") {
                    decompiledMainLoop(989) `should equal` 1056
                }
                it("should calculate the result much faster") {
                    divisors(989).sum() `should equal` 1056
                }
            }
        }
    }
    describe("part 2") {
        given("exercise input") {
            val inputString = readResource("day19Input.txt")
            val commandsWithDeclaration = parseCommandsWithDeclaration(inputString)
            val startRegisters = listOf(1) + List(5) { 0 }
            it("should calculate the value of R4 before entering the main loop in line 01") {
                var r4 = 0
                executeCommands(commandsWithDeclaration.first, commandsWithDeclaration.second, startRegisters) { _, registers, ip ->
                    val enteringLoop = ip == 1
                    if (enteringLoop) {
                        r4 = registers[4]
                    }
                    enteringLoop
                }
                r4 `should equal` 10551389
            }
            it("should calculate the result much faster") {
                divisors(10551389).sum() `should equal` 10915260
            }
        }

    }

})
