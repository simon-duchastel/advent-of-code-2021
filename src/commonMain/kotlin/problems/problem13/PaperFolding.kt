package problems.problem13

/**
 * Performs [n] folds from the list [folds] over the paper with the dots it specified by the coordinates [dots].
 * Return the number of total number of dots visible after these folds, or null if either [n] > the size of [folds]
 * or [dots] is empty.
 */
fun visibleDotsAfterNFolds(dots: List<Pair<Int, Int>>, folds: List<Fold>, n: Int): Int? {
    return performNFolds(dots, folds, n)?.size
}

/**
 * Performs all [folds] from the list in order on the paper with dots specified by the coordinate pairs [dots].
 * Returns the list of all remaining dots and their new positions after folding, or null if [dots] is empty.
 */
fun performAllFolds(dots: List<Pair<Int, Int>>, folds: List<Fold>): List<Pair<Int, Int>>? {
    return performNFolds(dots, folds, n = folds.size)
}

/**
 * Performs [n] [folds] from the list in order on the paper with dots specified by the coordinate pairs [dots].
 * Returns the list of all remaining dots and their new positions after folding, or null if either
 * [n] > the size of [folds] or [dots] is empty.
 */

fun performNFolds(dots: List<Pair<Int, Int>>, folds: List<Fold>, n: Int): List<Pair<Int, Int>>? {
    // check that we aren't being asked to perform more folds than we have available and that our dots
    // list is at least 1 long
    if (n > folds.size) return null
    if (dots.isEmpty()) return null

    // find the paper size, ie. the largets x value and the largest y
    val paperSize = (dots.maxBy { it.first }?.first ?: return null) to (dots.maxBy { it.second }?.second ?: return null)
    val paper = TransparentPaper(dots.toSet(), paperSize)

    // fold over all values
    val acc: Pair<TransparentPaper, List<Fold>>? = paper to folds
    return (0 until n).fold(acc) { curAcc, _ ->
        // propogate the null, otherwise pull out the values from the pair
        if (curAcc == null) return@fold null
        val (curPaper, curFolds) = curAcc

        // take the next fold, or throw a null if we've run out of folds
        val nextFold = curFolds.firstOrNull()
        if (nextFold == null) return@fold null

        // Perform a fold and propogate the results
        val newPaper = performFold(curPaper, nextFold)
        newPaper to curFolds.drop(1)
    }?.first?.dots?.toList() // of the results, we only care about the dots - not any remaining folds
}

/**
 * Perform a fold operation specified by [fold] over the transparent paper specified
 * by [dots]. Note that since both the input and output dots are sets, duplicates are
 * not counted (ie. if 2 dots overlap, they are considered 1 dot).
 */
fun performFold(paper: TransparentPaper, fold: Fold): TransparentPaper {
    // for each dot, transform it based on the fold
    val newDots = paper.dots.fold(setOf<Pair<Int, Int>>()) { newDots, curDot ->
        val dotMoved = when (fold) {
            is Fold.X -> {
                if (curDot.first < fold.atLocation) {
                    curDot // if it's below the fold line, it doesn't move
                } else {
                    // however many points away from the it is from the fold line, it moves that many past the line
                    val moveBy = curDot.first - fold.atLocation
                    (fold.atLocation - moveBy) to curDot.second
                }
            }
            is Fold.Y -> {
                if (curDot.second < fold.atLocation) {
                    curDot // if it's below the fold line, it doesn't move
                } else {
                    // however many points away from the it is from the fold line, it moves that many past the line
                    val moveBy = curDot.second - fold.atLocation
                    curDot.first to (fold.atLocation - moveBy)
                }
            }
        }
        newDots + dotMoved
    }

    // each time the paper is folded, it loses a row or column and then is halfed (depending on the axis)
    val newSize = when (fold) {
        is Fold.X -> ((paper.size.first - 1) / 2) to paper.size.second
        is Fold.Y -> paper.size.second to ((paper.size.second - 1) / 2)
    }

    // return the results
    return TransparentPaper(newDots, newSize)
}

/**
 * Represents a piece of transparent paper composed of [dots]
 */
data class TransparentPaper(val dots: Set<Pair<Int, Int>>, val size: Pair<Int, Int>)

/**
 * Represents a Fold operation that can be performed on the paper, composed of
 * the direction of the fold (X or Y) and the location of the fold on the paper.
 */
sealed class Fold(open val atLocation: Int) {
    data class X(override val atLocation: Int): Fold(atLocation)
    data class Y(override val atLocation: Int): Fold(atLocation)
}
