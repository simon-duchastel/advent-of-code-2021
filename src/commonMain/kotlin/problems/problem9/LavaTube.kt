package problems.problem9

/**
 * Find the product of the sizes of the [numBasins] largest basins in [map], where a basin is a group
 * of [LavaTubeHeight]s that eventually flow down to a single smallest value. The size of
 * the basin is the number of lava tubes in the basin.
 */
fun findProductOfLargestBasins(map: LavaTubeMap, numBasins: Int): Int {
    // Find each lowest lava tube position - each corresponds to a basin
    val lowestSelector = lowestLavaTubeSelector(map)
    val lowestLavaTubePositions: List<Pair<Int, Int>> = map.foldIndexed(emptyList()) { rowIndex, lowestListRow, row ->
        row.foldIndexed(lowestListRow) { columnIndex, lowestList, lavaTube ->
            if (lowestSelector(rowIndex, columnIndex, lavaTube)) {
                lowestList + (rowIndex to columnIndex)
            } else {
                lowestList
            }
        }
    }

    // Walk each basin to find its size
    val basinSizes = lowestLavaTubePositions.map { getBasin(map, it).size }

    // Take the numBasins largest basins and return the product of their sizes
    return basinSizes.sortedDescending().take(numBasins).fold(1) { product, size -> product * size }
}

/**
 * Given a [startingPoint] represented as (row, column) and a [map], return the the basin as the set of lava tube
 * positions comprising the basin. [startingPoint] should correspond to the lowest point in the basin - otherwise,
 * this result will be smaller than the true size of the basin.
 */
fun getBasin(map: LavaTubeMap, startingPoint: Pair<Int, Int>): Set<Pair<Int, Int>> {
    val (row, column) = startingPoint
    val currentHeight = map[row][column]

    // For each of the upwards, downwards, leftwards, and rightwards directions, see if the lava tube in that
    // direction is higher than this one and is not equal to 9 (these are the edges of basins). If so, recurse in that direction.
    // Use MIN_VALUE to ignore edges - this will cause the condition to default to false, which will stop the recursion.
    val upValue = map.getOrNull(row - 1)?.getOrNull(column) ?: Int.MIN_VALUE
    val upBasin = if (upValue < 9 && upValue > currentHeight) {
        getBasin(map, row - 1 to column)
    } else emptySet()
    val downValue = map.getOrNull(row + 1)?.getOrNull(column) ?: Int.MIN_VALUE
    val downBasin = if (downValue < 9 && downValue > currentHeight) {
        getBasin(map, row + 1 to column)
    } else emptySet()
    val leftValue = map.getOrNull(row)?.getOrNull(column - 1) ?: Int.MIN_VALUE
    val leftBasin = if (leftValue < 9 && leftValue > currentHeight) {
        getBasin(map, row to column - 1)
    } else emptySet()
    val rightValue = map.getOrNull(row)?.getOrNull(column + 1) ?: Int.MIN_VALUE
    val rightBasin = if (rightValue < 9 && rightValue > currentHeight ) {
        getBasin(map, row to column + 1)
    } else emptySet()

    // Sum all the recursions up, and add one to take this current lava tube into account for the basin size.
    return upBasin + downBasin + leftBasin + rightBasin + startingPoint
}

/**
 * Finds the risk level of all of the selected lava tubes in [map].
 * [selector] returns true when the lava tube should be used to calculate risk, false otherwise.
 * [riskValue] calculates the risk value of a selected lava tube to be added to the total risk sum.
 */
fun findRiskLevelOfTube(map: LavaTubeMap,
                        selector: (rowIndex: Int, columnIndex: Int, lavaTube: LavaTubeHeight) -> Boolean,
                        riskValue: (rowIndex: Int, columnIndex: Int, lavaTube: LavaTubeHeight) -> Int): Int {
    return map.foldIndexed(0) { rowIndex, rowSum, row ->
        row.foldIndexed(rowSum) { columnIndex, sum, lavaTube ->
            if (selector(rowIndex, columnIndex, lavaTube)) {
                sum + riskValue(rowIndex, columnIndex, lavaTube) // This lava tube has been selected - add its risk to the sum
            } else {
                sum
            }
        }
    }
}

/**
 * Selector for finding the lowest lava tube out of all of its neighbors. Given a map, will return a
 * selector function for finding the lowest lava tube between all of its neighbors (returning true if
 * it is the lowest, false otherwise).
 */
fun lowestLavaTubeSelector(map: LavaTubeMap) = { rowIndex: Int, columnIndex: Int, lavaTube: LavaTubeHeight ->
    // The lava tube is selected if it's lower than all 4 of its direct neighbors (ignoring neighbors off the edge).
    // In our if statements, default the value to MAX_VALUE since this will cause the check to be true (allowing
    // us to ignore the edges, which don't exist and thus return null)
    lavaTube < map.getOrNull(rowIndex - 1)?.getOrNull(columnIndex) ?: Int.MAX_VALUE &&
            lavaTube < map.getOrNull(rowIndex)?.getOrNull(columnIndex - 1) ?: Int.MAX_VALUE &&
            lavaTube < map.getOrNull(rowIndex + 1)?.getOrNull(columnIndex) ?: Int.MAX_VALUE &&
            lavaTube < map.getOrNull(rowIndex)?.getOrNull(columnIndex + 1) ?: Int.MAX_VALUE
}

/**
 * Finds the risk level of the lowest lava tubes in [map]. Calculates risk using a single risk value function,
 * where the risk is the height of the lava tube + 1
 */
fun findSingleRiskValueOfLowestLavaTubes(map: LavaTubeMap): Int =
    findRiskLevelOfTube(map, lowestLavaTubeSelector(map)) { _, _, lavaTube -> lavaTube + 1 } // the risk value is the height + 1



/**
 * Represents a map of lava tubes, specifically of their heights.
 */
typealias LavaTubeMap = List<List<LavaTubeHeight>>

/**
 * Represents the height of a lava tube, with 0 being lowest and 9 being highest.
 */
typealias LavaTubeHeight = Int