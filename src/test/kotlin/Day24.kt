import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import kotlin.math.ceil
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 24: Immune System Simulator 20XX ---

After a weird buzzing noise, you appear back at the man's cottage.
He seems relieved to see his friend, but quickly notices that the little reindeer caught some kind of cold
while out exploring.

The portly man explains that this reindeer's immune system isn't similar to regular reindeer immune systems:

The immune system and the infection each have an army made up of several groups;
each group consists of one or more identical units.
The armies repeatedly fight until only one army has units remaining.

Units within a group all have the same hit points (amount of damage a unit can take before it is destroyed),
attack damage (the amount of damage each unit deals), an attack type,
an initiative (higher initiative units attack first and win ties), and sometimes weaknesses or immunities.

Here is an example group:

18 units each with 729 hit points (weak to fire; immune to cold, slashing)
with an attack that does 8 radiation damage at initiative 10

Each group also has an effective power: the number of units in that group multiplied by their attack damage.
The above group has an effective power of 18 * 8 = 144.
Groups never have zero or negative units; instead, the group is removed from combat.

Each fight consists of two phases: target selection and attacking.

During the target selection phase, each group attempts to choose one target.
In decreasing order of effective power, groups choose their targets;
in a tie, the group with the higher initiative chooses first.
The attacking group chooses to target the group in the enemy army to which it would deal the most damage
(after accounting for weaknesses and immunities,
but not accounting for whether the defending group has enough units to actually receive all of that damage).

If an attacking group is considering two defending groups to which it would deal equal damage,
it chooses to target the defending group with the largest effective power;
if there is still a tie, it chooses the defending group with the highest initiative.

If it cannot deal any defending groups damage, it does not choose a target.
Defending groups can only be chosen as a target by one attacking group.

At the end of the target selection phase, each group has selected zero or one groups to attack,
and each group is being attacked by zero or one groups.

During the attacking phase, each group deals damage to the target it selected, if any.
Groups attack in decreasing order of initiative,
regardless of whether they are part of the infection or the immune system.
(If a group contains no units, it cannot attack.)

The damage an attacking group deals to a defending group depends on the attacking group's attack type
and the defending group's immunities and weaknesses.
By default, an attacking group would deal damage equal to its effective power to the defending group.
However, if the defending group is immune to the attacking group's attack type,
the defending group instead takes no damage;
if the defending group is weak to the attacking group's attack type, the defending group instead takes double damage.

The defending group only loses whole units from damage;
damage is always dealt in such a way that it kills the most units possible,
and any remaining damage to a unit that does not immediately kill it is ignored.

For example, if a defending group contains 10 units with 10 hit points each and receives 75 damage,
it loses exactly 7 units and is left with 3 units at full health.

After the fight is over, if both armies still contain units, a new fight begins;
combat only ends once one army has lost all of its units.

For example, consider the following armies:

Immune System:
17 units each with 5390 hit points (weak to radiation, bludgeoning) with
 an attack that does 4507 fire damage at initiative 2
989 units each with 1274 hit points (immune to fire; weak to bludgeoning,
 slashing) with an attack that does 25 slashing damage at initiative 3

Infection:
801 units each with 4706 hit points (weak to radiation) with an attack
 that does 116 bludgeoning damage at initiative 1
4485 units each with 2961 hit points (immune to radiation; weak to fire,
 cold) with an attack that does 12 slashing damage at initiative 4

If these armies were to enter combat, the following fights,
including details during the target selection and attacking phases, would take place:

Immune System:
Group 1 contains 17 units
Group 2 contains 989 units
Infection:
Group 1 contains 801 units
Group 2 contains 4485 units

Infection group 1 would deal defending group 1 185832 damage
Infection group 1 would deal defending group 2 185832 damage
Infection group 2 would deal defending group 2 107640 damage
Immune System group 1 would deal defending group 1 76619 damage
Immune System group 1 would deal defending group 2 153238 damage
Immune System group 2 would deal defending group 1 24725 damage

Infection group 2 attacks defending group 2, killing 84 units
Immune System group 2 attacks defending group 1, killing 4 units
Immune System group 1 attacks defending group 2, killing 51 units
Infection group 1 attacks defending group 1, killing 17 units

Immune System:
Group 2 contains 905 units
Infection:
Group 1 contains 797 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 184904 damage
Immune System group 2 would deal defending group 1 22625 damage
Immune System group 2 would deal defending group 2 22625 damage

Immune System group 2 attacks defending group 1, killing 4 units
Infection group 1 attacks defending group 2, killing 144 units
Immune System:
Group 2 contains 761 units
Infection:
Group 1 contains 793 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 183976 damage
Immune System group 2 would deal defending group 1 19025 damage
Immune System group 2 would deal defending group 2 19025 damage

Immune System group 2 attacks defending group 1, killing 4 units
Infection group 1 attacks defending group 2, killing 143 units
Immune System:
Group 2 contains 618 units
Infection:
Group 1 contains 789 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 183048 damage
Immune System group 2 would deal defending group 1 15450 damage
Immune System group 2 would deal defending group 2 15450 damage

Immune System group 2 attacks defending group 1, killing 3 units
Infection group 1 attacks defending group 2, killing 143 units
Immune System:
Group 2 contains 475 units
Infection:
Group 1 contains 786 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 182352 damage
Immune System group 2 would deal defending group 1 11875 damage
Immune System group 2 would deal defending group 2 11875 damage

Immune System group 2 attacks defending group 1, killing 2 units
Infection group 1 attacks defending group 2, killing 142 units
Immune System:
Group 2 contains 333 units
Infection:
Group 1 contains 784 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 181888 damage
Immune System group 2 would deal defending group 1 8325 damage
Immune System group 2 would deal defending group 2 8325 damage

Immune System group 2 attacks defending group 1, killing 1 unit
Infection group 1 attacks defending group 2, killing 142 units
Immune System:
Group 2 contains 191 units
Infection:
Group 1 contains 783 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 181656 damage
Immune System group 2 would deal defending group 1 4775 damage
Immune System group 2 would deal defending group 2 4775 damage

Immune System group 2 attacks defending group 1, killing 1 unit
Infection group 1 attacks defending group 2, killing 142 units
Immune System:
Group 2 contains 49 units
Infection:
Group 1 contains 782 units
Group 2 contains 4434 units

Infection group 1 would deal defending group 2 181424 damage
Immune System group 2 would deal defending group 1 1225 damage
Immune System group 2 would deal defending group 2 1225 damage

Immune System group 2 attacks defending group 1, killing 0 units
Infection group 1 attacks defending group 2, killing 49 units
Immune System:
No groups remain.
Infection:
Group 1 contains 782 units
Group 2 contains 4434 units

In the example above, the winning army ends up with 782 + 4434 = 5216 units.

You scan the reindeer's condition (your puzzle input); the white-bearded man looks nervous.
As it stands now, how many units would the winning army have?

--- Part Two ---

Things aren't looking good for the reindeer. The man asks whether more milk and cookies would help you think.

If only you could give the reindeer's immune system a boost, you might be able to change the outcome of the combat.

A boost is an integer increase in immune system units' attack damage.
For example, if you were to boost the above example's immune system's units by 1570,
the armies would instead look like this:

Immune System:
17 units each with 5390 hit points (weak to radiation, bludgeoning) with
 an attack that does 6077 fire damage at initiative 2
989 units each with 1274 hit points (immune to fire; weak to bludgeoning,
 slashing) with an attack that does 1595 slashing damage at initiative 3

Infection:
801 units each with 4706 hit points (weak to radiation) with an attack
 that does 116 bludgeoning damage at initiative 1
4485 units each with 2961 hit points (immune to radiation; weak to fire,
 cold) with an attack that does 12 slashing damage at initiative 4
With this boost, the combat proceeds differently:

Immune System:
Group 2 contains 989 units
Group 1 contains 17 units
Infection:
Group 1 contains 801 units
Group 2 contains 4485 units

Infection group 1 would deal defending group 2 185832 damage
Infection group 1 would deal defending group 1 185832 damage
Infection group 2 would deal defending group 1 53820 damage
Immune System group 2 would deal defending group 1 1577455 damage
Immune System group 2 would deal defending group 2 1577455 damage
Immune System group 1 would deal defending group 2 206618 damage

Infection group 2 attacks defending group 1, killing 9 units
Immune System group 2 attacks defending group 1, killing 335 units
Immune System group 1 attacks defending group 2, killing 32 units
Infection group 1 attacks defending group 2, killing 84 units
Immune System:
Group 2 contains 905 units
Group 1 contains 8 units
Infection:
Group 1 contains 466 units
Group 2 contains 4453 units

Infection group 1 would deal defending group 2 108112 damage
Infection group 1 would deal defending group 1 108112 damage
Infection group 2 would deal defending group 1 53436 damage
Immune System group 2 would deal defending group 1 1443475 damage
Immune System group 2 would deal defending group 2 1443475 damage
Immune System group 1 would deal defending group 2 97232 damage

Infection group 2 attacks defending group 1, killing 8 units
Immune System group 2 attacks defending group 1, killing 306 units
Infection group 1 attacks defending group 2, killing 29 units
Immune System:
Group 2 contains 876 units
Infection:
Group 2 contains 4453 units
Group 1 contains 160 units

Infection group 2 would deal defending group 2 106872 damage
Immune System group 2 would deal defending group 2 1397220 damage
Immune System group 2 would deal defending group 1 1397220 damage

Infection group 2 attacks defending group 2, killing 83 units
Immune System group 2 attacks defending group 2, killing 427 units
After a few fights...

Immune System:
Group 2 contains 64 units
Infection:
Group 2 contains 214 units
Group 1 contains 19 units

Infection group 2 would deal defending group 2 5136 damage
Immune System group 2 would deal defending group 2 102080 damage
Immune System group 2 would deal defending group 1 102080 damage

Infection group 2 attacks defending group 2, killing 4 units
Immune System group 2 attacks defending group 2, killing 32 units
Immune System:
Group 2 contains 60 units
Infection:
Group 1 contains 19 units
Group 2 contains 182 units

Infection group 1 would deal defending group 2 4408 damage
Immune System group 2 would deal defending group 1 95700 damage
Immune System group 2 would deal defending group 2 95700 damage

Immune System group 2 attacks defending group 1, killing 19 units
Immune System:
Group 2 contains 60 units
Infection:
Group 2 contains 182 units

Infection group 2 would deal defending group 2 4368 damage
Immune System group 2 would deal defending group 2 95700 damage

Infection group 2 attacks defending group 2, killing 3 units
Immune System group 2 attacks defending group 2, killing 30 units
After a few more fights...

Immune System:
Group 2 contains 51 units
Infection:
Group 2 contains 40 units

Infection group 2 would deal defending group 2 960 damage
Immune System group 2 would deal defending group 2 81345 damage

Infection group 2 attacks defending group 2, killing 0 units
Immune System group 2 attacks defending group 2, killing 27 units
Immune System:
Group 2 contains 51 units
Infection:
Group 2 contains 13 units

Infection group 2 would deal defending group 2 312 damage
Immune System group 2 would deal defending group 2 81345 damage

Infection group 2 attacks defending group 2, killing 0 units
Immune System group 2 attacks defending group 2, killing 13 units
Immune System:
Group 2 contains 51 units
Infection:
No groups remain.
This boost would allow the immune system's armies to win! It would be left with 51 units.

You don't even know how you could boost the reindeer's immune system or what effect it might have,
so you need to be cautious and find the smallest boost that would allow the immune system to win.

How many units does the immune system have left after getting the smallest boost it needs to win?

 */

fun parseArmies(input: String): Pair<ImmuneSystemArmy, InfectionArmy> {
    val inputLines = input.split("\n")
    val immuneSystemLines = mutableListOf<String>()
    val infectionLines = mutableListOf<String>()
    var parseImmuneSystem: Boolean? = null
    inputLines.forEach { line ->
        when {
            line.isBlank() -> {} // Ignore
            line.startsWith("Immune System:") -> parseImmuneSystem = true
            line.startsWith("Infection:") -> parseImmuneSystem = false
            else -> when {
                parseImmuneSystem == null -> throw IllegalArgumentException("Input lines without header")
                parseImmuneSystem!! -> immuneSystemLines += line
                else -> infectionLines += line
            }
        }
    }
    return Pair(
            ImmuneSystemArmy(*(immuneSystemLines.map { parseGroupLine(it) }.toTypedArray())),
            InfectionArmy(*(infectionLines.map { parseGroupLine(it) }.toTypedArray()))
    )
}

fun parseGroupLine(input: String): Group {
    val regex = """(\d+) units each with (\d+) hit points( \(([a-z,; ]+)\))? with an attack that does (\d+) ([a-z]+) damage at initiative (\d+)""".toRegex()
    val match = regex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
    require(match.groupValues.size == 8) { "${match.groupValues.size} elements parsed $input" }
    val values = match.groupValues
    val attackType = AttackType.valueOf(values[6].toUpperCase())
    val properties = parseProperties(values[4])
    return Group(units = values[1].toInt(), hitPoints = values[2].toInt(), weaknesses = properties.first, immunities = properties.second, attackDamage = values[5].toInt(), attackType = attackType, initiative = values[7].toInt())
}

fun parseProperties(input: String): Pair<Set<AttackType>, Set<AttackType>> {
    return if (input.isNotBlank()) {
        val weaknessesRegex = """(.*weak to ([a-z, ]+).*)?""".toRegex()
        val weaknessesMatch = weaknessesRegex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
        val weaknesses = if (weaknessesMatch.groupValues.size == 3) {
            val weaknessesString = weaknessesMatch.groupValues[2]
            parseAttackTypes(weaknessesString)
        } else emptySet()
        val immunitiesRegex = """(.*immune to ([a-z, ]+).*)?""".toRegex()
        val immunitiesMatch = immunitiesRegex.find(input) ?: throw IllegalArgumentException("Can not parse input $input")
        val immunities = if (immunitiesMatch.groupValues.size == 3) {
            val immunitiesString = immunitiesMatch.groupValues[2]
            parseAttackTypes(immunitiesString)
        } else emptySet()
        Pair(weaknesses, immunities)
    } else Pair(emptySet(), emptySet())
}

private fun parseAttackTypes(attackTypesString: String): Set<AttackType> =
        attackTypesString.split(",")
                .mapNotNull { if (it.isNotBlank()) AttackType.valueOf(it.trim().toUpperCase()) else null }
                .toSet()


fun fightTilTheEnd(immuneSystem: ImmuneSystemArmy, infection: InfectionArmy) {
    while(immuneSystem.units > 0 && infection.units > 0) {
       val unitsBeforeFight = immuneSystem.units + infection.units
        fight(immuneSystem, infection)
        val unitsAfterFight =  immuneSystem.units + infection.units
        if (unitsBeforeFight == unitsAfterFight) break // Stagnation ends with a draw
    }
}

fun targetSelection(infectionArmy: InfectionArmy, immuneSystemArmy: ImmuneSystemArmy): List<Pair<Group, Group>> {
    val infectionGroupsWithTarget = choseTargets(infectionArmy, immuneSystemArmy)
    val immuneSystemGroupsWithTarget = choseTargets(immuneSystemArmy, infectionArmy)
    return infectionGroupsWithTarget + immuneSystemGroupsWithTarget
}

private fun choseTargets(attackerArmy: Army, attackedArmy: Army): List<Pair<Group, Group>> {
    val targets = attackedArmy.groups.toMutableSet()
    val attackerSelectionComparator = compareByDescending<Group> { it.effectivePower }.thenByDescending { it.initiative }
    return attackerArmy.groups.sortedWith(attackerSelectionComparator).mapNotNull { attackerGroup ->
        val attackedGroup = choseTarget(attackerGroup, targets)
        if (attackedGroup != null) {
            targets.remove(attackedGroup)
            attackerGroup to attackedGroup
        } else null
    }
}

fun choseTarget(attacker: Group, targets: Set<Group>): Group? {
    val targetsWithDamage = targets.map { it to attacker.calculateDamage(it) }
    val targetSelectionComparator = compareByDescending<Pair<Group, Int>> { it.second }
            .thenByDescending { it.first.effectivePower }
            .thenByDescending { it.first.initiative }
    return targetsWithDamage.filter { (_, damage) ->
            damage > 0 // Don't attack when no damage
        }
        .sortedWith(targetSelectionComparator)
        .firstOrNull()?.first
}

fun fight(immuneSystem: ImmuneSystemArmy, infection: InfectionArmy) {
    targetSelection(infection, immuneSystem).sortedByDescending { it.first.initiative }.forEach { (attacker, attacked) ->
        attack(attacker, attacked)
    }
    immuneSystem.groups.removeIf { it.units <= 0 }
    infection.groups.removeIf { it.units <= 0 }
}

fun attack(attacker: Group, attacked: Group) {
    val killings = attacker.calculateKilling(attacked)
    if (killings > attacked.units) attacked.units = 0
    else attacked.units -= killings
}

fun findBoost(immuneSystem: ImmuneSystemArmy, infection: InfectionArmy): Triple<Int, ImmuneSystemArmy, InfectionArmy> {
    var currBoost = 2_000
    var lowerBoost = 0
    var upperBoost = currBoost
    while(true) { // binary search
        val immuneSystemProbe = immuneSystem.boost(currBoost)
        val infectionProbe = infection.copy(groups = infection.groups.map { it.copy() }.toMutableList())
        fightTilTheEnd(immuneSystemProbe, infectionProbe)
        if (immuneSystemProbe.units > 0 && infectionProbe.units == 0) {
            if (currBoost == lowerBoost + 1) return Triple(currBoost, immuneSystemProbe, infectionProbe)
            upperBoost = currBoost
            currBoost = (currBoost + lowerBoost) / 2
        } else {
            lowerBoost = currBoost
            currBoost = ceil((currBoost + upperBoost) / 2.0).toInt()
        }
    }
}

sealed class Army(open val groups: MutableList<Group>) {
    val units
        get() = groups.map { it.units }.sum()

}
data class InfectionArmy(override val groups: MutableList<Group>) : Army(groups){
    constructor(vararg groups: Group) : this(groups.toMutableList())
}
data class ImmuneSystemArmy(override val groups: MutableList<Group>) : Army(groups){
    fun boost(boost: Int) = copy(groups = groups.map {
        it.copy(attackDamage = it.attackDamage + boost)
    }.toMutableList())

    constructor(vararg groups: Group) : this(groups.toMutableList())
}

data class Group(var units: Int,
                 val hitPoints: Int,
                 val immunities: Set<AttackType> = emptySet(),
                 val weaknesses: Set<AttackType> = emptySet(),
                 val attackDamage: Int,
                 val attackType: AttackType,
                 val initiative: Int) {
    fun calculateDamage(attacked: Group): Int =
            when (attackType) {
                in attacked.weaknesses -> effectivePower * 2
                in attacked.immunities -> 0
                else -> effectivePower
            }

    fun calculateKilling(attacked: Group) = calculateKilling(calculateDamage(attacked), attacked)
    fun calculateKilling(damage: Int, attacked: Group): Int = damage / attacked.hitPoints

    val effectivePower
        get() = units * attackDamage
}

enum class AttackType {
    RADIATION, BLUDGEONING, FIRE, SLASHING, COLD
}

class Day24Spec : Spek({

    describe("part 1") {
        describe("example") {
            val immuneSystem = ImmuneSystemArmy(
                    Group(units = 17,
                            hitPoints = 5390,
                            weaknesses = setOf(AttackType.RADIATION, AttackType.BLUDGEONING),
                            attackDamage = 4507, attackType = AttackType.FIRE,
                            initiative = 2
                    ),
                    Group(units = 989,
                            hitPoints = 1274,
                            immunities = setOf(AttackType.FIRE),
                            weaknesses = setOf(AttackType.BLUDGEONING, AttackType.SLASHING),
                            attackDamage = 25, attackType = AttackType.SLASHING,
                            initiative = 3
                    )
            )
            val infection = InfectionArmy(
                    Group(units = 801,
                            hitPoints = 4706,
                            weaknesses = setOf(AttackType.RADIATION),
                            attackDamage = 116, attackType = AttackType.BLUDGEONING,
                            initiative = 1
                    ),
                    Group(units = 4485,
                            hitPoints = 2961,
                            immunities = setOf(AttackType.RADIATION),
                            weaknesses = setOf(AttackType.FIRE, AttackType.COLD),
                            attackDamage = 12, attackType = AttackType.SLASHING,
                            initiative = 4
                    )
            )
            it("should be created correctly") {
                immuneSystem.groups.size `should equal` 2
                with(immuneSystem.groups[1]) {
                    units `should equal` 989
                    hitPoints `should equal` 1274
                    immunities `should equal` setOf(AttackType.FIRE)
                    weaknesses `should equal` setOf(AttackType.BLUDGEONING, AttackType.SLASHING)
                    attackDamage `should equal` 25
                    attackType `should equal` AttackType.SLASHING
                }
            }
            it("should have the right effective power") {
                immuneSystem.groups[0].effectivePower `should equal` 76619
                immuneSystem.groups[1].effectivePower `should equal` 24725
            }
            describe("calculate damage") {
                val testData = arrayOf(
                        data(infection.groups[0], immuneSystem.groups[0], 185832),
                        data(infection.groups[0], immuneSystem.groups[1], 185832),
                        data(infection.groups[1], immuneSystem.groups[0], 53820),
                        data(infection.groups[1], immuneSystem.groups[1], 107640),
                        data(immuneSystem.groups[0], infection.groups[0], 76619),
                        data(immuneSystem.groups[0], infection.groups[1], 153238),
                        data(immuneSystem.groups[1], infection.groups[0], 24725),
                        data(immuneSystem.groups[1], infection.groups[1], 24725),
                        data(immuneSystem.groups[0], immuneSystem.groups[1], 0)
                )

                onData("attacking %s attacked %s", with = *testData) { attacking, attacked, expected ->
                    val result = attacking.calculateDamage(attacked)
                    it("should calculate damage as $expected") {
                        result `should equal` expected
                    }
                }
            }
            describe("calculate killing") {
                val testData = arrayOf(
                        data(infection.groups[1], immuneSystem.groups[1], 84),
                        data(immuneSystem.groups[0], infection.groups[1], 51)
                )

                onData("attacking %s attacked %s", with = *testData) { attacking, attacked, expected ->
                    val result = attacking.calculateKilling(attacked)
                    it("should calculate killing as $expected") {
                        result `should equal` expected
                    }
                }
            }
            describe("target selection") {
                on("target selection for infection army and immune system army") {
                    val targets = targetSelection(infection, immuneSystem)
                    it("should have found the correct sequence of attacks") {
                        targets.sortedByDescending { it.first.initiative } `should equal` listOf(
                                infection.groups[1] to immuneSystem.groups[1],
                                immuneSystem.groups[1] to infection.groups[0],
                                immuneSystem.groups[0] to infection.groups[1],
                                infection.groups[0] to immuneSystem.groups[0]
                        )
                    }
                }
            }
            on("fight") {
                fight(immuneSystem, infection)
                it("should have the expected result") {
                    immuneSystem.groups.map { it.units } `should equal` listOf(905)
                    infection.groups.map { it.units } `should equal` listOf(797, 4434)
                }
            }
            //In the example above, the winning army ends up with 782 + 4434 = 5216 units.
            on("fight until the end") {
                fightTilTheEnd(immuneSystem, infection)
                it("should have the expected result") {
                    immuneSystem.units `should equal` 0
                    infection.units `should equal` 5216
                }
            }
            describe("parse input") {
                given("a simple input") {
                    val input = "2743 units each with 4149 hit points with an attack that does 13 radiation damage at initiative 14"
                    it("should be parsed correctly") {
                        parseGroupLine(input) `should equal` Group(units = 2743, hitPoints = 4149, weaknesses = emptySet(), immunities = emptySet(), attackDamage = 13, attackType = AttackType.RADIATION, initiative = 14)
                    }
                }
                given("an input with one weakness") {
                    val input = "262 units each with 8499 hit points (weak to cold) with an attack that does 45 cold damage at initiative 6"
                    it("should be parsed correctly") {
                        parseGroupLine(input) `should equal` Group(units = 262, hitPoints = 8499, weaknesses = setOf(AttackType.COLD), immunities = emptySet(), attackDamage = 45, attackType = AttackType.COLD, initiative = 6)
                    }
                }
                given("an input with two weaknesses") {
                    val input = "262 units each with 8499 hit points (weak to cold, fire) with an attack that does 45 cold damage at initiative 6"
                    it("should be parsed correctly") {
                        parseGroupLine(input) `should equal` Group(units = 262, hitPoints = 8499, weaknesses = setOf(AttackType.COLD, AttackType.FIRE), immunities = emptySet(), attackDamage = 45, attackType = AttackType.COLD, initiative = 6)
                    }
                }
                given("an input with one immunity") {
                    val input = "262 units each with 8499 hit points (immune to cold) with an attack that does 45 cold damage at initiative 6"
                    it("should be parsed correctly") {
                        parseGroupLine(input) `should equal` Group(units = 262, hitPoints = 8499, weaknesses = emptySet(), immunities = setOf(AttackType.COLD), attackDamage = 45, attackType = AttackType.COLD, initiative = 6)
                    }
                }
                given("an input with weaknesses and immunities") {
                    val input = "262 units each with 8499 hit points (weak to cold, fire; immune to slashing, radiation, bludgeoning) with an attack that does 45 cold damage at initiative 6"
                    it("should be parsed correctly") {
                        parseGroupLine(input) `should equal` Group(units = 262, hitPoints = 8499, weaknesses = setOf(AttackType.COLD, AttackType.FIRE), immunities = setOf(AttackType.SLASHING, AttackType.RADIATION, AttackType.BLUDGEONING), attackDamage = 45, attackType = AttackType.COLD, initiative = 6)
                    }
                }
                given("an input with weaknesses and immunities reversed") {
                    val input = "262 units each with 8499 hit points (immune to slashing, radiation, bludgeoning; weak to cold, fire) with an attack that does 45 cold damage at initiative 6"
                    it("should be parsed correctly") {
                        parseGroupLine(input) `should equal` Group(units = 262, hitPoints = 8499, weaknesses = setOf(AttackType.COLD, AttackType.FIRE), immunities = setOf(AttackType.SLASHING, AttackType.RADIATION, AttackType.BLUDGEONING), attackDamage = 45, attackType = AttackType.COLD, initiative = 6)
                    }
                }
                given("an input with immune system and infection") {
                    val input = """
                        Immune System:
                        2743 units each with 4149 hit points with an attack that does 13 radiation damage at initiative 14
                        8829 units each with 7036 hit points with an attack that does 7 fire damage at initiative 15
                        
                        Infection:
                        262 units each with 8499 hit points (weak to cold) with an attack that does 45 cold damage at initiative 6
                        732 units each with 47014 hit points (weak to cold, bludgeoning) with an attack that does 127 bludgeoning damage at initiative 17
                    """.trimIndent()
                    it("should be parsed correctly") {
                        val (immuneSystemTest, infectionTest) = parseArmies(input)
                        immuneSystemTest `should equal` ImmuneSystemArmy(
                                Group(units = 2743, hitPoints = 4149, attackDamage = 13, attackType = AttackType.RADIATION, initiative = 14),
                                Group(units = 8829, hitPoints = 7036, attackDamage = 7, attackType = AttackType.FIRE, initiative = 15)
                        )
                        infectionTest `should equal` InfectionArmy(
                                Group(units = 262, hitPoints = 8499, weaknesses = setOf(AttackType.COLD), attackDamage = 45, attackType = AttackType.COLD, initiative = 6),
                                Group(units = 732, hitPoints = 47014, weaknesses = setOf(AttackType.COLD, AttackType.BLUDGEONING), attackDamage = 127, attackType = AttackType.BLUDGEONING, initiative = 17)
                        )
                    }
                }
            }
            given("example input") {
                val input = """
                        Immune System:
                        17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
                        989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3
                        
                        Infection:
                        801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
                        4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4 
                    """.trimIndent()
                val (immuneSystemTest, infectionTest) = parseArmies(input)
                on("fight until the end") {
                    fightTilTheEnd(immuneSystemTest, infectionTest)
                    it("should have the expected result") {
                        immuneSystemTest.units `should equal` 0
                        infectionTest.units `should equal` 5216
                    }
                }
            }
        }
        describe("exercise") {
            given("exercise input") {
                val input = readResource("day24Input.txt")
                val (immuneSystem, infection) = parseArmies(input)
                on("fight until the end") {
                    fightTilTheEnd(immuneSystem, infection)
                    it("should have the expected result") {
                        immuneSystem.units `should equal` 0
                        infection.units `should equal` 24318
                    }
                }
            }
        }
    }
    describe("part 2") {
        describe("example") {
            val input = """
                Immune System:
                17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
                989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3
                
                Infection:
                801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
                4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4 
            """.trimIndent()
            given("boosted immune system") {
                val (immuneSystem, infection) = parseArmies(input)
                val boostedImmuneSystem = immuneSystem.boost(1570)
                on("fight until the end") {
                    fightTilTheEnd(boostedImmuneSystem, infection)
                    it("should have the expected result") {
                        boostedImmuneSystem.units `should equal` 51
                        infection.units `should equal` 0
                    }
                }
            }
            on("find boost") {
                val (immuneSystem, infection) = parseArmies(input)
                val result = findBoost(immuneSystem, infection)
                it("should have found the boost") {
                    result.first `should equal` 1570
                    result.second.units `should equal` 51
                    result.third.units `should equal` 0
                }
            }
        }
        describe("exercise") {
            given("exercise input") {
                val input = readResource("day24Input.txt")
                val (immuneSystem, infection) = parseArmies(input)
                on("find boost") {
                    val result = findBoost(immuneSystem, infection)
                    it("should have found the boost") {
                        result.first `should equal` 79
                        result.second.units `should equal` 1083
                        result.third.units `should equal` 0

                    }
                }
            }
        }
    }
})
