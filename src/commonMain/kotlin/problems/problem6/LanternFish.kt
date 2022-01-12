package problems.problem6

/**
 * Returns the total number of fish after [numDays] in the fish population seeded by [startingFish].
 * Uses a simulation of fish population over time to calculate the total number of fish.
 * Note that this is inefficient for large numbers of fish or large numbers of days. Consider using
 * [findTotalFishWithOptimizedSimulation]
 */
fun findTotalFishWithSimulation(startingFish: List<LanternFish>, numDays: Int): Int {
    return (1..numDays).fold(startingFish) { fishPopulation, _ ->
        // Find the number of new fish to be added to the pool.
        val numNewFish = fishPopulation.fold(0) { numNew, fish ->
            if (fish.canSpawnNewFish()) numNew + 1 else numNew
        }

        // Find all the updated population of fish, not including newly spawned fish.
        val updatedFishPopulation = fishPopulation.map {
            if (it.daysUntilSpawn == 0) {
                LanternFish.NewlyRefreshedFish
            } else {
                LanternFish(it.daysUntilSpawn - 1)
            }
        }

        // Return the updated fish population with newly spawned fish added
        updatedFishPopulation.plus(List(numNewFish) { LanternFish.NewlySpawnedFish })
    }.size // Return the number of fish in the final fish population
}

/**
 * Returns the total number of fish after [numDays] in the fish population seeded by [startingFish].
 * Uses a calculation based on the initial starting fish and the total number of days to find the
 * precise number of fish after [numDays]. This algorithm is efficient even with large numbers of fish or days.
 */
fun findTotalFishWithOptimizedSimulation(startingFish: List<LanternFish>, numDays: Int): Long {
    // Create an array storing the number of fish at each day of their cycle. Note that we use Long to account
    // for potentially very large sizes in the number of fish.
    val fishArray = Array(LanternFish.CYCLE_LENGTH_NEWLY_SPAWNED) { days -> startingFish.count { it.daysUntilSpawn == days }.toLong() }
    println("INITIAL ARRAY IS ${fishArray.joinToString(" ") { it.toString() }}")
    (1..numDays).forEach { _ ->
        // Every day, move all of the fish over 1 day and re-add all of the 0-day fish (ready to spawn) at the corresponding
        // place in the array on the right. Additionally, add an equal number of fish to account for new spawns.
        val fishReadyToSpawn = fishArray[0]
        for (i in 1 until fishArray.size) {
            fishArray[i - 1] = fishArray[i]
        }
        fishArray[fishArray.size - 1] = 0 // Set the end of the array to 0 to account for those fish moving down 1 day.
        fishArray[LanternFish.CYCLE_LENGTH - 1] += fishReadyToSpawn
        fishArray[LanternFish.CYCLE_LENGTH_NEWLY_SPAWNED - 1] += fishReadyToSpawn
    }
    return fishArray.sum() // Return the sum of all fish in all states
}

/**
 * Represents a LanternFish, with a certain number of days left in its cycle before it's ready to
 * spawn a new LanternFish.
 */
inline class LanternFish(val daysUntilSpawn: Int) {

    /**
     * Returns true if this [LanternFish] is ready to spawn a new [LanternFish], false otherwise.
     */
    fun canSpawnNewFish(): Boolean = daysUntilSpawn == 0

    companion object {
        const val CYCLE_LENGTH = 7
        const val CYCLE_LENGTH_NEWLY_SPAWNED = 9

        /**
         * Converts an [input] String representation into a [LanternFish], or false if it cannot be converted.
         * To be in valid format, [input] must be a valid Integer.
         */
        fun fromString(input: String): LanternFish? {
            return input.trim().toIntOrNull()?.let { LanternFish(it) }
        }

        /**
         * Represents a newly-spawned [LanternFish].
         */
        val NewlySpawnedFish = LanternFish(CYCLE_LENGTH_NEWLY_SPAWNED - 1) // subtract 1 to account for zero-index

        /**
         * Represents a newly-refreshed [LanternFish] ie. after it's days-until-spawn value has reached 0.
         */
        val NewlyRefreshedFish = LanternFish(CYCLE_LENGTH - 1) // subtract 1 to account for zero-index
    }
}