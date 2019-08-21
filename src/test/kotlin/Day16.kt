import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should contain all`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 16: Chronal Classification ---

As you see the Elves defend their hot chocolate successfully, you go back to falling through time.
This is going to become a problem.

If you're ever going to return to your own time, you need to understand how this device on your wrist works.
You have a little while before you reach your next destination, and with a bit of trial and error,
you manage to pull up a programming manual on the device's tiny screen.

According to the manual, the device has four registers (numbered 0 through 3)
that can be manipulated by instructions containing one of 16 opcodes. The registers start with the value 0.

Every instruction consists of four values:
an opcode, two inputs (named A and B), and an output (named C), in that order.
The opcode specifies the behavior of the instruction and how the inputs are interpreted.
The output, C, is always treated as a register.

In the opcode descriptions below, if something says "value A", it means to take the number given as A literally.
(This is also called an "immediate" value.)
If something says "register A", it means to use the number given as A to read from (or write to)
the register with that number.

So, if the opcode addi adds register A and value B, storing the result in register C,
and the instruction addi 0 7 3 is encountered, it would add 7 to the value contained by register 0
and store the sum in register 3, never modifying registers 0, 1, or 2 in the process.

Many opcodes are similar except for how they interpret their arguments. The opcodes fall into seven general categories:

Addition:

addr (add register) stores into register C the result of adding register A and register B.
addi (add immediate) stores into register C the result of adding register A and value B.

Multiplication:

mulr (multiply register) stores into register C the result of multiplying register A and register B.
muli (multiply immediate) stores into register C the result of multiplying register A and value B.

Bitwise AND:

banr (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B.
bani (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B.

Bitwise OR:

borr (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B.
bori (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B.

Assignment:

setr (set register) copies the contents of register A into register C. (Input B is ignored.)
seti (set immediate) stores value A into register C. (Input B is ignored.)

Greater-than testing:

gtir (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0.
gtri (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0.
gtrr (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0.

Equality testing:

eqir (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0.
eqri (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0.
eqrr (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0.

Unfortunately, while the manual gives the name of each opcode, it doesn't seem to indicate the number.
However, you can monitor the CPU to see the contents of the registers before
and after instructions are executed to try to work them out.

Each opcode has a number from 0 through 15, but the manual doesn't say which is which.
For example, suppose you capture the following sample:

Before: [3, 2, 1, 1]
9 2 1 2
After:  [3, 2, 2, 1]

This sample shows the effect of the instruction 9 2 1 2 on the registers.
Before the instruction is executed, register 0 has value 3, register 1 has value 2, and registers 2 and 3 have value 1.
After the instruction is executed, register 2's value becomes 2.

The instruction itself, 9 2 1 2, means that opcode 9 was executed with A=2, B=1, and C=2.
Opcode 9 could be any of the 16 opcodes listed above, but only three of them behave in a way
that would cause the result shown in the sample:

Opcode 9 could be mulr: register 2 (which has a value of 1) times register 1 (which has a value of 2) produces 2,
which matches the value stored in the output register, register 2.
Opcode 9 could be addi: register 2 (which has a value of 1) plus value 1 produces 2,
which matches the value stored in the output register, register 2.
Opcode 9 could be seti: value 2 matches the value stored in the output register, register 2;
the number given for B is irrelevant.
None of the other opcodes produce the result captured in the sample. Because of this, the sample above behaves like three opcodes.

You collect many of these samples (the first section of your puzzle input).
The manual also includes a small test program (the second section of your puzzle input) - you can ignore it for now.

Ignoring the opcode numbers, how many samples in your puzzle input behave like three or more opcodes?

--- Part Two ---

Using the samples you collected, work out the number of each opcode and execute the test program
(the second section of your puzzle input).

What value is contained in register 0 after executing the test program?

 */

abstract class Command(val opCode: Int) {
    abstract fun execute(registers: List<Int>): List<Int> // Very modern cpu architecture: registers are immutable
    fun fetch(immediate: Boolean, parameter: Int, registers: List<Int>) = if (immediate) parameter else registers[parameter]
}

fun <E> Iterable<E>.updated(index: Int, elem: E) = mapIndexed { i, existing ->  if (i == index) elem else existing }
    // See discussion https://discuss.kotlinlang.org/t/best-way-to-replace-an-element-of-an-immutable-list/8646/7

data class Addi(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) + fetch(true, b, registers)
        return registers.updated(c, result)
    }
}
data class Addr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) + fetch(false, b, registers)
        return registers.updated(c, result)
    }
}
data class Muli(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) * fetch(true, b, registers)
        return registers.updated(c, result)
    }
}
data class Mulr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) * fetch(false, b, registers)
        return registers.updated(c, result)
    }
}
data class Bani(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) and fetch(true, b, registers)
        return registers.updated(c, result)
    }
}
data class Banr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) and fetch(false, b, registers)
        return registers.updated(c, result)
    }
}
data class Bori(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) or fetch(true, b, registers)
        return registers.updated(c, result)
    }
}
data class Borr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) or fetch(false, b, registers)
        return registers.updated(c, result)
    }
}
data class Setr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers)
        return registers.updated(c, result)
    }
}
data class Seti(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(true, a, registers)
        return registers.updated(c, result)
    }
}
data class Gtir(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(true, a, registers) > fetch(false, b, registers)
        return registers.updated(c, if (result) 1 else 0)
    }
}
data class Gtri(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) > fetch(true, b, registers)
        return registers.updated(c, if (result) 1 else 0)
    }
}
data class Gtrr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) > fetch(false, b, registers)
        return registers.updated(c, if (result) 1 else 0)
    }
}
data class Eqir(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(true, a, registers) == fetch(false, b, registers)
        return registers.updated(c, if (result) 1 else 0)
    }
}
data class Eqri(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) == fetch(true, b, registers)
        return registers.updated(c, if (result) 1 else 0)
    }
}
data class Eqrr(val a: Int, val b: Int, val c: Int) : Command(0) {
    override fun execute(registers: List<Int>): List<Int> {
        val result = fetch(false, a, registers) == fetch(false, b, registers)
        return registers.updated(c, if (result) 1 else 0)
    }
}

val allCommands = listOf(Addr::class, Addi::class, Mulr::class, Muli::class,
                        Banr::class, Bani::class, Borr::class, Bori::class,
                        Setr::class, Seti::class,
                        Gtir::class, Gtri::class, Gtrr::class,
                        Eqir::class, Eqri::class, Eqrr::class)


fun parseRegisters(input: String): List<Int> {
    val regex = """(Before: |After: +)\[(\d+), (\d+), (\d+), (\d+)]""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
    if (match.groupValues.size != 6) throw IllegalArgumentException("Only ${match.groupValues.size} elements parsed $input")
    val values = match.groupValues
    return listOf(values[2].toInt(), values[3].toInt(), values[4].toInt(), values[5].toInt())
}

fun parseOpcodes(input: String): List<Int> {
    val regex = """(\d+) (\d+) (\d+) (\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
    if (match.groupValues.size != 5) throw IllegalArgumentException("Only ${match.groupValues.size} elements parsed $input")
    val values = match.groupValues
    return listOf(values[1].toInt(), values[2].toInt(), values[3].toInt(), values[4].toInt())
}

fun parseCodeSamples(input: String): List<CodeSample> =
        input.split("\n")
                .mapIndexed { i, line ->
                    when(i % 4) {
                        0 -> parseRegisters(line)
                        1 -> parseOpcodes(line)
                        2 -> parseRegisters(line)
                        else -> null
                    }
                }
                .chunked(4)
                .map { chunk ->
                    val chunkNotNull = chunk.filterNotNull()
                    CodeSample(chunkNotNull[0], chunkNotNull[1], chunkNotNull[2])
                }


fun parseCode(inputCode: String): List<List<Int>>  =
        inputCode.split("\n")
                .map { parseOpcodes(it) }

fun compile(opcodeMap: Map<Int, KClass<out Command>>, code: List<List<Int>>): List<Command> =
        code.map { opcode ->
            val commandKlass = opcodeMap[opcode[0]] ?: throw IllegalAccessException("No command found for opcode ${opcode[0]}")
            val params = opcode.drop(1)
            val array = params.toTypedArray()

            commandKlass.primaryConstructor!!.call(* array)
        }

fun executeCode(opcodeMap: Map<Int, KClass<out Command>>, code: List<List<Int>>): List<Int> {
    val compiledCode = compile(opcodeMap, code)
    val startRegisters = List(4) { 0 }
    return compiledCode.fold(startRegisters) { registers: List<Int>, command: Command ->
        command.execute(registers)
    }
}

fun solveOpCodeMap(codeSamples: List<CodeSample>): Map<Int, KClass<out Command>> {
    val result = mutableMapOf<Int, KClass<out Command>>()
    val alreadySolved = mutableSetOf<KClass<out Command>>()
    val opCodesWithPossibleCommands = codeSamples.map { it.opcode[0] to it.findCommands(allCommands) }.toMap()
    while(true) {
        val nextMap = solveUniqueOpCodeMap(opCodesWithPossibleCommands, alreadySolved)
        if (nextMap.isEmpty()) return result
        result.putAll(nextMap)
        alreadySolved.addAll(nextMap.values)
    }
}

fun solveUniqueOpCodeMap(opCodesWithPossibleCommands: Map<Int, List<KClass<out Command>>>, alreadySolved: Set<KClass<out Command>>): Map<Int, KClass<out Command>> =
        opCodesWithPossibleCommands.entries
                .filter { it.value.filter { it !in alreadySolved }.size == 1 }
                .distinctBy { it.key }
                .map{it.key to it.value.filter { it !in alreadySolved }.first() }
                .toMap()

data class CodeSample(val before: List<Int>, val opcode: List<Int>, val after: List<Int>) {
    fun checkCommand(commandKlass: KClass<out Command>): Boolean {
        val params = opcode.drop(1)
        val array = params.toTypedArray()

        val command = commandKlass.primaryConstructor!!.call(* array)
        return command.execute(before) == after
    }

    fun countPossibleCommands(allCommands: List<KClass<out Command>>) = allCommands.count { checkCommand(it) }

    fun findCommands(allCommands: List<KClass<out Command>>) = allCommands.filter { checkCommand(it) }
}

class Day16Spec : Spek({

    describe("part 1") {
        describe("execute addi commands") {
            given("registers") {
                val startRegisters = listOf(42, 0, 0, 0)
                it("should change registers correctly") {
                    Addi(0, 7, 3).execute(startRegisters) `should equal` listOf(42, 0, 0, 49)
                }
            }
        }
        describe("three opcodes") {
            given("registers") {
                val startRegisters = listOf(3, 2, 1, 1)
                val expectedRegisters = listOf(3, 2, 2, 1)
                val threeOpcodes = arrayOf(
                        data(Mulr(2, 1, 2) as Command, expectedRegisters),
                        data(Addi(2, 1, 2) as Command, expectedRegisters),
                        data(Seti(2, 1, 2) as Command, expectedRegisters)
                )
                onData("command %s", with = *threeOpcodes) { command: Command, expected: List<Int> ->
                    val result = command.execute(startRegisters)
                    it("returns $expected") {
                        result `should equal` expected
                    }
                }

            }
        }
        describe("all opcodes") {
            given("registers") {
                val startRegisters = listOf(3, 2, 1, 1)
                val threeOpcodes = arrayOf(
                        data(Addr(2, 1, 2) as Command, listOf(3, 2, 3, 1)),
                        data(Addi(2, 1, 2) as Command, listOf(3, 2, 2, 1)),
                        data(Mulr(2, 1, 2) as Command, listOf(3, 2, 2, 1)),
                        data(Muli(2, 5, 3) as Command, listOf(3, 2, 1, 5)),
                        data(Banr(2, 1, 2) as Command, listOf(3, 2, 0, 1)),
                        data(Bani(2, 5, 3) as Command, listOf(3, 2, 1, 1)),
                        data(Borr(2, 1, 2) as Command, listOf(3, 2, 3, 1)),
                        data(Bori(2, 5, 3) as Command, listOf(3, 2, 1, 5)),
                        data(Setr(0, 1, 2) as Command, listOf(3, 2, 3, 1)),
                        data(Seti(2, 1, 2) as Command, listOf(3, 2, 2, 1)),
                        data(Gtir(0, 1, 3) as Command, listOf(3, 2, 1, 0)),
                        data(Gtri(2, 0, 3) as Command, listOf(3, 2, 1, 1)),
                        data(Gtrr(2, 1, 3) as Command, listOf(3, 2, 1, 0)),
                        data(Eqir(0, 1, 3) as Command, listOf(3, 2, 1, 0)),
                        data(Eqri(0, 3, 3) as Command, listOf(3, 2, 1, 1)),
                        data(Eqrr(3, 2, 3) as Command, listOf(3, 2, 1, 1))

                )
                onData("command %s", with = *threeOpcodes) { command: Command, expected: List<Int> ->
                    val result = command.execute(startRegisters)
                    it("returns $expected") {
                        result `should equal` expected
                    }
                }

            }
        }
        describe("parse input") {
            given("before input line") {
                val input = "Before: [3, 2, 1, 1]"
                it("should be parsed to a list of registers") {
                    parseRegisters(input) `should equal` listOf(3, 2, 1, 1)
                }
            }
            given("opcode line") {
                val input = "9 2 1 2"
                it("should be parsed to a list of op codes") {
                    parseOpcodes(input) `should equal` listOf(9, 2, 1, 2)
                }
            }
            given("example input") {
                val input = """
                    Before: [3, 2, 1, 1]
                    9 2 1 2
                    After:  [3, 2, 2, 1]

                """.trimIndent()
                it("should parse input correctly") {
                    parseCodeSamples(input) `should equal` listOf(CodeSample(
                            before = listOf(3, 2, 1, 1),
                            opcode = listOf(9, 2, 1, 2),
                            after = listOf(3, 2, 2, 1)
                    ))
                }
            }
        }
        describe("find possible commands") {
            given("example input") {
                val input = """
                    Before: [3, 2, 1, 1]
                    9 2 1 2
                    After:  [3, 2, 2, 1]

                """.trimIndent()
                val beforeAfter = parseCodeSamples(input)[0]
                it("should find possible commands") {
                    allCommands.filter { command ->
                        beforeAfter.checkCommand(command)
                    } `should contain all` listOf( Mulr::class, Addi::class, Seti::class)
                }
            }
        }
        describe("count possible commands") {
            given("example input") {
                val input = """
                    Before: [3, 2, 1, 1]
                    9 2 1 2
                    After:  [3, 2, 2, 1]

                """.trimIndent()
                val beforeAfter = parseCodeSamples(input)[0]
                it("should count possible commands") {
                    beforeAfter.countPossibleCommands(allCommands) `should equal` 3
                }
            }
        }
        describe("exercise") {
            given("exercise input") {
                val input = readResource("day16Input1.txt")
                val codeSamples = parseCodeSamples(input)
                it("should have parsed right number of before afters") {
                    codeSamples.size `should equal` 793
                }
                val filter3orMore = codeSamples.map { it.countPossibleCommands(allCommands) }.filter { it >= 3}
                it("should cound samples with 3 or more matching opcodes") {
                    filter3orMore.size `should equal` 493
                }
            }
        }
    }
    describe("part 2") {
        describe("solve op code map") {
            given("a unique input") {
                val input = """
                    Before: [3, 2, 3, 2]
                    2 1 3 3
                    After:  [3, 2, 3, 3]

                """.trimIndent()
                val codeSamples = parseCodeSamples(input)
                it("should find resulting map") {
                    val opcodeMap = solveOpCodeMap(codeSamples)
                    opcodeMap `should equal` mapOf(2 to Bori::class)
                }
            }
            given("a not so unique input") {
                val input = """
                    Before: [3, 2, 3, 2]
                    2 1 3 3
                    After:  [3, 2, 3, 3]

                    Before: [0, 3, 0, 2]
                    4 2 1 0
                    After:  [1, 3, 0, 2]

                """.trimIndent()
                val codeSamples = parseCodeSamples(input)
                it("should find resulting map") {
                    val opcodeMap = solveOpCodeMap(codeSamples)
                    opcodeMap `should equal` mapOf(2 to Bori::class, 4 to Addi::class)
                }
            }
        }
        describe("parse code") {
            given("some input code") {
                val inputCode = """
                    1 0 0 1
                    4 1 1 1
                    14 0 0 3
                """.trimIndent()
                it("should be parsed correctly") {
                    parseCode(inputCode) `should equal` listOf(
                            listOf( 1, 0, 0, 1),
                            listOf( 4, 1, 1, 1),
                            listOf(14, 0, 0, 3)
                    )
                }
            }
        }
        given("exercise input") {
            val input = readResource("day16Input1.txt")
            val codeSamples = parseCodeSamples(input)
            it("should calculate which samples have only 1 matching opcode") {
                val filter1 = codeSamples.map { it.countPossibleCommands(allCommands) }.filter { it == 1}
                filter1.size `should equal` 46
            }
            it("should find unique match") {
                val opcodeMap = codeSamples.map { it.opcode[0] to it.findCommands(allCommands) }
                        .filter { it.second.size == 1 }
                        .distinctBy { it.first }
                        .map{it.first to it.second.first() }
                        .toMap()
                opcodeMap `should equal` mapOf(2 to Bori::class) // 2 -> bori
            }
            it("should find next match") {
                val alreadySolved = setOf(Bori::class)
                val opcodeMap = codeSamples.map { it.opcode[0] to it.findCommands(allCommands) }
                        .filter { it.second.filter { it !in alreadySolved }.size == 1 }
                        .distinctBy { it.first }
                        .map{it.first to it.second.minus(Bori::class).first() }
                        .toMap()
                opcodeMap `should equal` mapOf(4 to Addi::class, 1 to Muli::class)
            }
            on("finding the code map matching to the samples") {
                val opcodeMap = solveOpCodeMap(codeSamples)
                it("should have found the right map of commands") {
                    opcodeMap `should equal` mapOf(
                            2 to Bori::class, 4 to Addi::class, 13 to Mulr::class, 1 to Muli::class, 11 to Addr::class,
                            8 to Borr::class, 14 to Seti::class, 7 to Gtir::class, 0 to Banr::class, 10 to Bani::class,
                            3 to Setr::class, 9 to Eqri::class, 15 to Gtrr::class, 12 to Eqir::class, 6 to Gtri::class,
                            5 to Eqrr::class
                    )
                }
                val testCodeInput = """
                                    1 0 0 1
                                    4 1 1 1
                                    14 0 0 3
                                """.trimIndent()
                // MULI 0 0 1 # 0 0 0 0
                // ADDI 1 1 1 # 0 1 0 0
                // SETI 0 0 3 # 0 1 0 0
                val testCode = parseCode(testCodeInput)
                it("should execute test code correctly") {
                    val result = executeCode(opcodeMap, testCode)
                    result `should equal` listOf(0, 1, 0, 0)
                }
                it("should execute the sample code") {
                    val inputCode = readResource("day16Input2.txt")
                    val code = parseCode(inputCode)
                    val registers = executeCode(opcodeMap, code)
                    val register0 = registers.first()
                    register0 `should equal` 445
                }
            }
        }
    }

})
