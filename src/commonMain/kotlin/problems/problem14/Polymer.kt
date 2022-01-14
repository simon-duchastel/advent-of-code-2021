package problems.problem14

/**
 * Returns the difference between the number of occurrences of the most frequent polymer segment and least
 * frequent polymer segment after performing [numInsertions] insertions of the rules defined by [insertions]
 * over the given [startingPolymer]. Uses a simulation algorithm to simulate how many pairs of polymer segments
 * exist after [numInsertions].
 * Returns null if [startingPolymer] is empty or [numInsertions] is smaller than 1.
 */
fun findDifferenceInElementsSimulation(startingPolymer: Polymer, insertions: List<InsertionRule>, numInsertions: Int): Long? {
    if (startingPolymer.isEmpty() || numInsertions < 1) return null

    val pairs = (1 until startingPolymer.size)
        .map { index -> startingPolymer[index - 1] to startingPolymer[index]  } // create list of pairs
        .groupBy() { it } // group those pairs together into a map
        .mapValues { (_, value) -> value.size.toLong() } // the values of the maps are the list of pairs, but we want their count

    val finalPairsMap: Map<Pair<Char, Char>, Long> = (0 until numInsertions).fold(pairs) { pairsMap, _ ->
        simulateInsertion(pairsMap, insertions)
    }

    val charCountMap = finalPairsMap
        .map { (pair, count) -> pair.first to count } // when counting the char, only take the first char in the pair to avoid double counting
        .fold(mapOf<Char, Long>()) { charMap, (char, count) ->
            val newCount = (charMap[char] ?: 0L) + count
            charMap + (char to newCount)
        }

    val mostCommonCount = charCountMap.maxBy { it.value }?.value ?: return null
    val leastCommonCount = charCountMap.minBy { it.value }?.value ?: return null

    return mostCommonCount - leastCommonCount
}

/**
 *  Simulates a single pass of polymer insertions on a polymer where [startingPairs] defines the count
 *  of each pair of segments exist (where the count is 0 if a pair does not exist in the map). Uses
 *  [insertions] Returns the resulting count of pairs as a map.
 */
fun simulateInsertion(startingPairs: Map<Pair<Char, Char>, Long>, insertions: List<InsertionRule>): Map<Pair<Char, Char>, Long> {
    return startingPairs.keys.fold(mapOf<Pair<Char, Char>, Long>()) { mapAcc, key ->
        // for each pair we're keeping track of, increase the count of the new pairs afer mapping the existing pair
        // to the existing transformation it will have
        val charToInsert = insertions.find { it.pair == key }?.let { it.insertion }
        if (charToInsert != null) {
            val firstPair = key.first to charToInsert
            val secondPair = charToInsert to key.second

            val existingPairCount = startingPairs[key]!! // this should never be null
            val firstPairCount = mapAcc[firstPair] ?: 0L
            val secondPairCount = mapAcc[secondPair] ?: 0L
            if (firstPair == secondPair) {
                // consider the special case where all the characters are the same, and thus firstPair == secondPair (we
                // don't want to overwrite the entry in the map)
                mapAcc + (firstPair to firstPairCount + (existingPairCount * 2L))
            } else {
                mapAcc + (firstPair to firstPairCount + existingPairCount) + (secondPair to secondPairCount + existingPairCount)
            }
        } else {
            mapAcc // if no insertion rules match, nothing is inserted and the pairs don't change
        }
    }
}

/**
 * Returns the difference between the number of occurrences of the most frequent polymer segment and least
 * frequent polymer segment after performing [numInsertions] insertions of the rules defined by [insertions]
 * over the given [startingPolymer]. Uses a brute force algorithm to perform each insertion.
 * Returns null if [startingPolymer] is empty or [numInsertions] is smaller than 1.
 */
fun findDifferenceInElementsBruteForce(startingPolymer: Polymer, insertions: List<InsertionRule>, numInsertions: Int): Int? {
    if (startingPolymer.isEmpty() || numInsertions < 1) return null

    // Perform numInsertion insertions
    val finalPolymer = (0 until numInsertions).fold(startingPolymer) { polymer, _ ->
        performPolymerInsertionBruteForce(polymer, insertions)
    }

    // Return the difference in the count between the most and least common elements
    val mapOfSegments = finalPolymer.groupingBy { it }.eachCount()
    val mostCommonCount = mapOfSegments.maxBy { it.value }?.value ?: return null
    val leastCommonCount = mapOfSegments.minBy { it.value }?.value ?: return null
    return mostCommonCount - leastCommonCount
}

/**
 *  Performs a single pass of polymer insertions on the provided [startingPolymer] and using
 *  the provided list of insertion rules [insertions]. Returns the resulting [Polymer].
 *  Uses a brute-force algorithm to perform the polymer insertion.
 */
fun performPolymerInsertionBruteForce(startingPolymer: Polymer, insertions: List<InsertionRule>): Polymer {
    // Going through each pair of polymer segments, evaluate all of our insertion rules and
    // insert any polymer segments as necessary.
    return (1 until startingPolymer.size).fold(listOf(startingPolymer[0])) { polymerAcc, index ->
        // If an insertion rule matches, insert the character before appending the next character. Otherwise, just
        // append the next character and move on.
        val targetPair = startingPolymer[index - 1] to startingPolymer[index]
        val charToInsert = insertions.find { it.pair == targetPair }?.let { it.insertion }
        if (charToInsert != null) {
            polymerAcc + charToInsert + targetPair.second
        } else {
            polymerAcc + targetPair.second
        }
    }
}

/**
 * Represents a Polymer, which is simply an ordered list of characters.
 */
typealias Polymer = List<Char>

/**
 * Represents an insertion rule. When a pair of consecutive polymer segments represented
 * by [pair] are found, insert [insertion] between those two segments.
 */
data class InsertionRule(val pair: Pair<Char, Char>, val insertion: Char)