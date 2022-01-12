package problems.problem11

/**
 * Simulates [n] number of steps of Octopi flashing, given the
 * inital map [initialCavern] of Octopi. Returns the final state after [n]
 * steps.
 */
fun simulateOctopusSteps(n: Int, initialCavern: OctopusCavern): OctopusState {
    // call nextStep on the initial octupus state n times, and return the result
    val initialState = OctopusState(octupusMap = initialCavern)
    return (0 until n).fold(initialState) { state: OctopusState, _: Int ->
        state.nextStep()
    }
}

/**
 * Find the first step where [condition] is true for the resuling OctopusState starting
 * from [initialCavern].
 * If [condition] is true for [initialCavern], then 0 is returned.
 */
fun findFirstStepForCondition(initialCavern: OctopusCavern, condition: (OctopusState) -> Boolean): Int {
    // call nextStep until the condition is true
    var state = OctopusState(octupusMap = initialCavern)
    var steps = 0
    while (!condition(state)) {
        steps++
        state = state.nextStep()
    }
    return steps
}

/**
 * Find the first step where the OctopusState stemming from a starting state of
 * [initialCavern] has all octopi flash simultaneously.
 * If [initialCavern] has all octopi flash simulatenously, then 0 is returned (meaning
 * the simulatenous flash occurred before a step could be executed).
 */
fun findFirstSimultaneousFlash(initialCavern: OctopusCavern): Int =
    findFirstStepForCondition(initialCavern) { state ->
        state.octupusMap.octopi.all { row -> row.all { octopi -> octopi.energy == 0 } }
    } // find the first step where all the energies are 0 (meaning all the octopi just flashed together)

/**
 * Represents a snapshot of an Octopus state, ie. the map
 * of Octopi and the total number of times the Octopi in the map
 * have flashed.
 */
data class OctopusState(
    val octupusMap: OctopusCavern,
    val totalFlashes: Int = 0
) {

    /**
     * Moves forward one step, producing a new [OctopusState] object of
     * the result after the step.
     */
    fun nextStep(): OctopusState {
        // define a local function for performing a flash on each Octopus
        var newFlashes = 0
        fun performFlash(cavern: OctopusCavern, position: Pair<Int, Int>): OctopusCavern {
            // helper function for a common operation
            fun OctopusCavern.toMutableList() = this.octopi.map { it.toMutableList() }.toMutableList()

            val (i, j) = position
            var newCavern: MutableList<MutableList<Octopus>> = cavern.toMutableList()

            // if the octopus flahed, set it as such and increase the energy of each surrounding octopi
            if (!newCavern[i][j].didFlash && newCavern[i][j].readyToFlash()) {
                newFlashes++
                newCavern[i][j] = newCavern[i][j].copy(didFlash = true)

                // increase each adjacent octupi's energy and recurse (but only if it hasn't already flashed)
                if (i - 1 in newCavern.indices && !newCavern[i-1][j].didFlash) {
                    newCavern[i-1][j] = newCavern[i-1][j].copy(energy = newCavern[i-1][j].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i-1 to j).toMutableList()
                }
                if (i + 1 in newCavern.indices && !newCavern[i+1][j].didFlash) {
                    newCavern[i+1][j] = newCavern[i+1][j].copy(energy = newCavern[i+1][j].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i+1 to j).toMutableList()
                }
                if (j - 1 in newCavern[i].indices && !newCavern[i][j-1].didFlash) {
                    newCavern[i][j-1] = newCavern[i][j-1].copy(energy = newCavern[i][j-1].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i to j-1).toMutableList()
                }
                if (j + 1 in newCavern[i].indices && !newCavern[i][j+1].didFlash) {
                    newCavern[i][j+1] = newCavern[i][j+1].copy(energy = newCavern[i][j+1].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i to j+1).toMutableList()
                }
                if (i - 1 in newCavern.indices && j - 1 in newCavern[i-1].indices && !newCavern[i-1][j-1].didFlash) {
                    newCavern[i-1][j-1] = newCavern[i-1][j-1].copy(energy = newCavern[i-1][j-1].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i-1 to j-1).toMutableList()
                }
                if (i - 1 in newCavern.indices && j + 1 in newCavern[i-1].indices && !newCavern[i-1][j+1].didFlash) {
                    newCavern[i-1][j+1] = newCavern[i-1][j+1].copy(energy = newCavern[i-1][j+1].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i-1 to j+1).toMutableList()
                }
                if (i + 1 in newCavern.indices && j - 1 in newCavern[i+1].indices && !newCavern[i+1][j-1].didFlash) {
                    newCavern[i+1][j-1] = newCavern[i+1][j-1].copy(energy = newCavern[i+1][j-1].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i+1 to j-1).toMutableList()
                }
                if (i + 1 in newCavern.indices && j + 1 in newCavern[i+1].indices && !newCavern[i+1][j+1].didFlash) {
                    newCavern[i+1][j+1] = newCavern[i+1][j+1].copy(energy = newCavern[i+1][j+1].energy + 1)
                    newCavern = performFlash(OctopusCavern(newCavern), i+1 to j+1).toMutableList()
                }
            }

            return OctopusCavern(newCavern)
        }

        // increase each of the octopi's energy by 1
        var newCavern = octupusMap.let { it.octopi.map { row -> row.map { octopus -> octopus.copy(octopus.energy + 1) }  } }

        // call our performFlash function over every octopus in the map, accumulating
        // a new map and the number of new flahses along the way.
        for (i in octupusMap.octopi.indices) {
            for (j in octupusMap.octopi[i].indices) {
                newCavern = performFlash(cavern = OctopusCavern(newCavern), position = i to j).octopi
            }
        }

        // set all octopi which have flashed back to 0
        newCavern = newCavern.map { row -> row.map { octopus ->
            if (octopus.didFlash) {
                octopus.copy(energy = 0, didFlash = false)
            } else {
                octopus
            }
        } }

        // return the result
        return OctopusState(octupusMap = OctopusCavern(newCavern), totalFlashes = totalFlashes + newFlashes)
    }
}

/**
 * Represents a cavern of Octopi.
 */
inline class OctopusCavern(val octopi: List<List<Octopus>>)

/**
 * Represents a single octupus, which has an energy level and an internal counter as
 * to whether it has flashed this step.
 */
data class Octopus(val energy: Int, val didFlash: Boolean = false) {
    /**
     * True if this octopus is ready to flash, false otherwise.
     */
    fun readyToFlash(): Boolean = energy > 9
}