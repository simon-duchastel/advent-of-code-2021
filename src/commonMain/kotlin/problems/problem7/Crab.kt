package problems.problem7

import kotlin.math.abs

/**
 * Find the optimal position for the [input] list of crabs to congregate given a linear fuel
 * cost for traveling each space. Returns the fuel expended to reach the optimal crab position
 * provided that each crab move costs 1 fuel, or null if [input] is empty.
 */
fun findOptimalCrabPositionLinear(input: List<Crab>): Int? = findOptimalCrabPosition(input = input) { it }

/**
 * Find the optimal position for the [input] list of crabs to congregate given an exponential fuel
 * cost for traveling each space. Returns the fuel expended to reach the optimal crab position
 * provided that each crab move costs 1 more fuel for each space the crab has already traveled, or
 * null if [input] is empty.
 */
fun findOptimalCrabPositionExponential(input: List<Crab>): Int? = findOptimalCrabPosition(input = input) { distance ->
    // Each new positional movement costs 1 more fuel than the last. Ie. cost = 1 + 2 + 3 + ... + n.
    // See here for an understanding of why this math works for this series: https://en.wikipedia.org/wiki/1_%2B_2_%2B_3_%2B_4_%2B_⋯
    distance * (distance + 1) / 2
}


/**
 * Find the optimal position for the [input] list of crabs to congregate given the provided [crabCost]
 * function for determining fuel cost for a given distance. Returns the fuel expended to reach the optimal
 * crab position, or null if [input] is empty.
 */
private fun findOptimalCrabPosition(input: List<Crab>, crabCost: (distance: Int) -> Int): Int? {
    if (input.isEmpty()) return null
    val allPossiblePositions = input.map { it.horizontalPosition }.let { it.min()!!..it.max()!! } // we can use !! since we checked input was non-empty
    return allPossiblePositions.map {  potentialPosition ->
        input.fold(0) { cost, crab -> cost + crabCost(abs(potentialPosition - crab.horizontalPosition)) }
    }.min()
}

/**
 * Represents a crab in a submarine with a given [horizontalPosition].
 */
inline class Crab(val horizontalPosition: Int) {
    companion object {
        /**
         * Converts an [input] String representation into a [LanternFish], or false if it cannot be converted.
         * To be in valid format, [input] must be a valid Integer.
         */
        fun fromString(input: String): Crab? {
            return input.trim().toIntOrNull()?.let { Crab(it) }
        }
    }
}