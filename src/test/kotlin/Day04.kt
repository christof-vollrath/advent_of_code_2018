import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/*
--- Day 4: Repose Record ---

You've sneaked into another supply closet - this time, it's across from the prototype suit manufacturing lab.
You need to sneak inside and fix the issues with the suit, but there's a guard stationed outside the lab,
so this is as close as you can safely get.

As you search the closet for anything that might help, you discover that you're not the first person to want to sneak in.
Covering the walls, someone has spent an hour starting every midnight for the past few months
secretly observing this guard post!
They've been writing down the ID of the one guard on duty that night -
the Elves seem to have decided that one guard was enough for the overnight shift -
as well as when they fall asleep or wake up while at their post (your puzzle input).

For example, consider the following records, which have already been organized into chronological order:

[1518-11-01 00:00] Guard #10 begins shift
[1518-11-01 00:05] falls asleep
[1518-11-01 00:25] wakes up
[1518-11-01 00:30] falls asleep
[1518-11-01 00:55] wakes up
[1518-11-01 23:58] Guard #99 begins shift
[1518-11-02 00:40] falls asleep
[1518-11-02 00:50] wakes up
[1518-11-03 00:05] Guard #10 begins shift
[1518-11-03 00:24] falls asleep
[1518-11-03 00:29] wakes up
[1518-11-04 00:02] Guard #99 begins shift
[1518-11-04 00:36] falls asleep
[1518-11-04 00:46] wakes up
[1518-11-05 00:03] Guard #99 begins shift
[1518-11-05 00:45] falls asleep
[1518-11-05 00:55] wakes up

Timestamps are written using year-month-day hour:minute format.
The guard falling asleep or waking up is always the one whose shift most recently started.
Because all asleep/awake times are during the midnight hour (00:00 - 00:59),
only the minute portion (00 - 59) is relevant for those events.

Visually, these records show that the guards are asleep at these times:

Date   ID   Minute
            000000000011111111112222222222333333333344444444445555555555
            012345678901234567890123456789012345678901234567890123456789
11-01  #10  .....####################.....#########################.....
11-02  #99  ........................................##########..........
11-03  #10  ........................#####...............................
11-04  #99  ....................................##########..............
11-05  #99  .............................................##########.....

The columns are Date, which shows the month-day portion of the relevant day;
ID, which shows the guard on duty that day; and Minute,
which shows the minutes during which the guard was asleep within the midnight hour.
(The Minute column's header shows the minute's ten's digit in the first row and the one's digit in the second row.)
Awake is shown as ., and asleep is shown as #.

Note that guards count as asleep on the minute they fall asleep,
and they count as awake on the minute they wake up.
For example, because Guard #10 wakes up at 00:25 on 1518-11-01, minute 25 is marked as awake.

If you can figure out the guard most likely to be asleep at a specific time,
you might be able to trick that guard into working tonight so you can have the best chance of sneaking in.
You have two strategies for choosing the best guard/minute combination.

Strategy 1: Find the guard that has the most minutes asleep. What minute does that guard spend asleep the most?

In the example above, Guard #10 spent the most minutes asleep, a total of 50 minutes (20+25+5),
while Guard #99 only slept for a total of 30 minutes (10+10+10).
Guard #10 was asleep most during minute 24
(on two days, whereas any other minute the guard was asleep was only seen on one day).

While this example listed the entries in chronological order, your entries are in the order you found them.
You'll need to organize them before they can be analyzed.

What is the ID of the guard you chose multiplied by the minute you chose?
(In the above example, the answer would be 10 * 24 = 240.)

--- Part Two ---

Strategy 2: Of all guards, which guard is most frequently asleep on the same minute?

In the example above, Guard #99 spent minute 45 asleep more than any other guard or minute - three times in total.
(In all other cases, any guard spent any minute asleep at most twice.)

What is the ID of the guard you chose multiplied by the minute you chose?
(In the above example, the answer would be 99 * 45 = 4455.)

 */


fun guardStrategy1(input: String) = detectIntervals(parseGuardRecords(input)).run {
    val mostAsleep = findMostAsleep(this)
    mostAsleep * findMinuteMostLikelyAsleep(this[mostAsleep]!!)
}

fun guardStrategy2(input: String) = detectIntervals(parseGuardRecords(input)).run {
    data class SleepEntry(val minute: Int, val timesAsleep: Int)
    val guardMap = map { (guard, sleepingIntervals) ->
        val sleepingMap = createSleepFrequencyMap(sleepingIntervals)
        val (minute, mostTimesAsleep) = sleepingMap.maxBy { (_, sleeping) -> sleeping }!!
        guard to SleepEntry(minute, mostTimesAsleep)
    }
    val (guard, sleepEntry) = guardMap.maxBy {(_, sleepEntry) -> sleepEntry.timesAsleep }!!
    guard * sleepEntry.minute
}

fun findMinuteMostLikelyAsleep(sleepingIntervals: List<SleepingInterval>): Int {
    val minutesMap = createSleepFrequencyMap(sleepingIntervals)
    return minutesMap.maxBy { (_, sleeping) -> sleeping }!!.key
}

private fun createSleepFrequencyMap(sleepingIntervals: List<SleepingInterval>) = mutableMapOf<Int, Int>().apply {
    sleepingIntervals.map { (from, to) ->
        (from..to).forEach {
            val sleep = this[it]
            this[it] = (sleep ?: 0) + 1
        }
    }
}

fun findMostAsleep(sleepingIntervalsPerGuard: Map<Int, List<SleepingInterval>>) = sumSleepingTimes(sleepingIntervalsPerGuard).entries.maxBy { (_, sleepingTime) ->  sleepingTime}!!.key

fun sumSleepingTimes(sleepingIntervalsPerGuard: Map<Int, List<SleepingInterval>>) = sleepingIntervalsPerGuard.entries.map { (guard, sleepingIntervals) ->
    guard to sleepingIntervals.map { it.to - it.from  + 1}.sum()
}.toMap()

fun detectIntervals(inputEvents: List<GuardEvent>): Map<Int, List<SleepingInterval>> {
    var currentGuard: Int? = null
    var sleepingStarted = 0
    return inputEvents.mapNotNull {
        when(it) {
            is BeginsShift -> { currentGuard = it.guardNr; null }
            is FallsAsleep -> { sleepingStarted = it.timeStamp.minute; null }
            is WakesUp -> {
                val guard = currentGuard ?: throw IllegalArgumentException("No guard to wake up")
                guard to SleepingInterval(sleepingStarted, it.timeStamp.minute - 1)
            }

        }
    }.groupBy({ it.first }, { it.second })
}

data class SleepingInterval(val from: Int, val to: Int)

fun parseGuardRecords(input: String) = input.split("\n")
        .map { parseGuardRecord(it)}
        .sortedBy { it.timeStamp }

fun parseGuardRecord(input: String): GuardEvent {
    val  regex = """\[(\d+)-(\d+)-(\d+) (\d+):(\d+)]\s*(.*)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
    if (match.groupValues.size != 7) throw IllegalArgumentException("Only ${match.groupValues.size} elements parsed $input")
    val values = match.groupValues
    val timeStamp = TimeStamp(values[1].toInt(), values[2].toInt(), values[3].toInt(), values[4].toInt(), values[5].toInt())
    return when (val report = values[6]) {
        "falls asleep" -> FallsAsleep(timeStamp)
        "wakes up" -> WakesUp(timeStamp)
        else -> {
            val reportRegex = """Guard #(\d+).*""".toRegex()
            val reportMatch = reportRegex.find(report) ?: throw IllegalArgumentException("Can not parse report $report")
            if (reportMatch.groupValues.size != 2) throw IllegalArgumentException("No guard nr in $report")
            BeginsShift(timeStamp, reportMatch.groupValues[1].toInt())
        }
    }
}

sealed class GuardEvent { abstract val timeStamp: TimeStamp }
data class BeginsShift(override val timeStamp: TimeStamp, val guardNr: Int) : GuardEvent()
data class FallsAsleep(override val timeStamp: TimeStamp) : GuardEvent()
data class WakesUp(override val timeStamp: TimeStamp) : GuardEvent()

data class TimeStamp(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int) : Comparable<TimeStamp> {
    override fun compareTo(other: TimeStamp): Int {
        if (this.year > other.year) return 1
        if (this.year < other.year) return -1
        if (this.month > other.month) return 1
        if (this.month < other.month) return -1
        if (this.day > other.day) return 1
        if (this.day < other.day) return -1
        if (this.hour > other.hour) return 1
        if (this.hour < other.hour) return -1
        if (this.minute > other.minute) return 1
        if (this.minute < other.minute) return -1
        return 0
    }
}

class Day04Spec : Spek({

    val exampleInput = """
                    [1518-11-01 00:00] Guard #10 begins shift
                    [1518-11-01 00:05] falls asleep
                    [1518-11-01 00:25] wakes up
                    [1518-11-01 00:30] falls asleep
                    [1518-11-01 00:55] wakes up
                    [1518-11-01 23:58] Guard #99 begins shift
                    [1518-11-02 00:40] falls asleep
                    [1518-11-02 00:50] wakes up
                    [1518-11-03 00:05] Guard #10 begins shift
                    [1518-11-03 00:24] falls asleep
                    [1518-11-03 00:29] wakes up
                    [1518-11-04 00:02] Guard #99 begins shift
                    [1518-11-04 00:36] falls asleep
                    [1518-11-04 00:46] wakes up
                    [1518-11-05 00:03] Guard #99 begins shift
                    [1518-11-05 00:45] falls asleep
                    [1518-11-05 00:55] wakes up
                """.trimIndent()

    describe("part 1") {
        given("some guards event as input") {
                val input = """
                [1518-11-01 00:00] Guard #10 begins shift
                [1518-11-01 00:05] falls asleep
                [1518-11-01 00:25] wakes up
                [1518-11-01 00:30] falls asleep
                [1518-11-01 00:55] wakes up
                [1518-11-01 23:58] Guard #99 begins shift
            """.trimIndent()
            it("should parse input to events and sorted by time") {
                parseGuardRecords(input) `should equal` listOf(
                        BeginsShift(TimeStamp(1518, 11,  1,  0,  0), 10),
                        FallsAsleep(TimeStamp(1518, 11,  1,  0,  5)),
                        WakesUp(    TimeStamp(1518, 11,  1,  0, 25)),
                        FallsAsleep(TimeStamp(1518, 11,  1,  0, 30)),
                        WakesUp(    TimeStamp(1518, 11,  1,  0, 55)),
                        BeginsShift(TimeStamp(1518, 11,  1, 23, 58), 99)
                )
            }
        }
        given("example input") {
            val inputEvents = parseGuardRecords(exampleInput)
            it("should find intervals") {
                detectIntervals(inputEvents) `should equal` mapOf(
                        10 to listOf(SleepingInterval(5, 24), SleepingInterval(30, 54), SleepingInterval(24, 28)),
                        99 to listOf(SleepingInterval(40, 49), SleepingInterval(36, 45), SleepingInterval(45, 54))
                )
            }
            it("should sum sleeping times") {
                sumSleepingTimes(detectIntervals(inputEvents)) `should equal` mapOf(
                        10 to 50,
                        99 to 30
                )
            }
            it("should guard sleeping most of the time") {
                findMostAsleep(detectIntervals(inputEvents)) `should equal` 10
            }
            it("should find minute guard is most likely asleep") {
                findMinuteMostLikelyAsleep(detectIntervals(inputEvents).getValue(10)) `should equal` 24
            }
            it("should find solution for strategy 1") {
                guardStrategy1(exampleInput) `should equal` 240
            }
        }
        given("exercise input") {
            val exerciseInput = readResource("day04Input.txt")
            guardStrategy1(exerciseInput) `should equal` 60438
        }
    }
    describe("part 2") {
        given("example input") {
            it("should find solution for strategy 2") {
                guardStrategy2(exampleInput) `should equal` 4455
            }
        }
        given("exercise input") {
            val exerciseInput = readResource("day04Input.txt")
            guardStrategy2(exerciseInput) `should equal` 47989
        }
    }
})

