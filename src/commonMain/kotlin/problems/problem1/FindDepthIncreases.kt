package problems.problem1

/**
 * Find the number of times 2 consecutive values in the [depths] array strictly increase.
 */
fun findDepthIncreasesPair(depths: List<Int>): Int {
    return depths
        .drop(1) // drop the first element since we're only interested in pairs of elements
        .foldIndexed(0) { index, acc, depth ->
            // Starting at 0, perform a fold operation over the list looking back at the previous element each time and
            // incrementing the accumulator by 1 every time a larger depth is found
            if (depth > depths[index]) { // since we dropped the first element, depths[index] is the previous
                acc + 1
            } else {
                acc
            }
        }
}

/**
 * Find the number of times 2 consecutive windows in the [depths] array strictly increase.
 * A window is the sum of values in a consecutive group of depth values with length [n].
 */
fun findDepthIncreasesNWindow(depths: List<Int>, n: Int): Int {
    // Construct a new list of the sums of all windows in the depths list
    return depths
        .drop(n) // drop the first n elements since we're interested in comparing pairs of windows (with 1 window being n elements long)
        .foldIndexed(0) { index, acc, depth ->
            // Starting at 0, perform a fold operation over the list looking back n elements each time and
            // incrementing the accumulator by 1 every time a larger depth is found. The difference in the sum of
            // the windows is equivalent to the difference between the first element of window w and the last element of
            // window w+1
            if (depth > depths[index]) { // since we dropped the first n elements, depths[index] is n elements behind
                acc + 1
            } else {
                acc
            }
        }
}