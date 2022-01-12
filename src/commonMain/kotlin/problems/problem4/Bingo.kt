package problems.problem4

/**
 * Play bingo on the list of [boards] with the list of [numbersToCall] until one or more boards win,
 * and then return the score of that winning board (or the highest winning score if multiple boards win).
 * [numbersToCall] must produce a valid winning board - otherwise null is returned.
 */
fun findBingoWinningScore(numbersToCall: List<Int>, boards: List<BingoBoard>): Int? {
    // Play Bingo until one or more boards have won
    var currentNumberIndex = 0
    while (boards.all { !it.hasWon() }) {
        if (currentNumberIndex >= numbersToCall.size) return null // if we've no more numbers to call, return null
        boards.forEach { it.markNumber(numbersToCall[currentNumberIndex]) }
        currentNumberIndex++
    }

    // Take the highest scoring winning-board (offset currentNumberIndex by one to account for the loop)
    return boards
        .filter { it.hasWon() }
        .map { it.calculateScore(numbersToCall[currentNumberIndex - 1]) }
        .max()
}

/**
 * Play bingo on the list of [boards] with the list of [numbersToCall] until all but one board has won,
 * and then return the score of that board.
 * [numbersToCall] must eventually allow all boards to win - otherwise, null is returned.
 */
fun findLastWinningBingoBoard(numbersToCall: List<Int>, boards: List<BingoBoard>): Int? {
    // Run through every number, marking the boards and looking for the last remaining non-winning board
    numbersToCall.fold(boards) { curBoards, curNumber ->
        curBoards.forEach { it.markNumber(curNumber) } // mark the number on each board

        // if the last board has won, return its score
        if (curBoards.size == 1 && curBoards.single().hasWon()) return curBoards.single().calculateScore(curNumber)

        val newBoards = curBoards.filter { !it.hasWon() } // keep only the boards which haven't yet won
        newBoards // continue the fold operation with our new curBoards array
    }
    return null // if we weren't able to find only one board remaining (ie. either 0 or > 1 remain), return null
}

/**
 * Represents a single Board composed of Bingo Tiles.
 */
class BingoBoard(private val tileRows: Array<Array<BingoTile>>) {
    /**
     * Returns true if this board has won, false otherwise.
     * Winning is defined as any row or column being fully marked.
     */
    fun hasWon(): Boolean {
        if (tileRows.any { row -> row.all { it.marked } }) return true // If any of the rows is fully marked, we won
        for (column in (0 until tileRows.size)) {
            if (tileRows.all { it[column].marked }) return true // If any of the columns is fully marked, we won
        }

        // If none of the above conditions are true, then we have not won
        return false
    }

    /**
     * Returns true if this board has won, false otherwise.
     */
    fun hasWonWithDiagonals(): Boolean {
        if (hasWon()) return true // if we've won on rows or columns, return true

        // If the first diagonal is completely marked, we won
        if (tileRows.foldIndexed(true) { index, allMarked, row ->
                if (row[index].marked) {
                    allMarked
                } else {
                    false
                }
            }) return true

        // If the second diagonal is completely marked, we won
        if (tileRows.foldIndexed(true) { index, allMarked, row ->
                if (row[row.size - index - 1].marked) { // invert the one of the indices to get the second diagonal
                    allMarked
                } else {
                    false
                }
            }) return true

        // If none of the above conditions are true, then we have not won
        return false
    }

    /**
     * Calculates the score of a Bingo Board based on the last number called.
     */
    fun calculateScore(lastNumberCalled: Int): Int {
        val unmarkedTiles = tileRows.sumBy { row -> row.sumBy { if (!it.marked) it.number else 0 } }
        return lastNumberCalled * unmarkedTiles
    }

    /**
     * Mark [numberToMark] on this Bingo Board.
     */
    fun markNumber(numberToMark: Int) {
        tileRows.forEach { row ->
            row.forEachIndexed { tileIndex, tile ->
                if (tile.number == numberToMark) {
                    row[tileIndex] = tile.copy(marked = true)
                }
            }
        }
    }

    /**
     * Return a String representation of this board
     */
    fun asString(): String {
        return tileRows.joinToString(separator = "\n") { row ->
            row.joinToString(separator = " ") { tile ->
                val buffer = if (tile.number < 10) " " else ""
                if (tile.marked) {
                    "[$buffer${tile.number}]"
                } else {
                    " $buffer${tile.number} "
                }
            }
        }
    }


    /**
     * Represents a Bingo Tile on a board.
     */
    data class BingoTile(
        val number: Int,
        val marked: Boolean
    )

    companion object {
        /**
         * Create a new board from a list [input] of integers representing the board tiles.
         * [input] must be [rowSize] * [rowSize] integers long to be a valid board, since each
         * board is square. Each The first row in the board is the first [rowSize] integers in [input],
         * the next row is the next [rowSize] integers, etc.
         */
        fun createNewBoard(input: List<Int>, rowSize: Int): BingoBoard? {
            if (input.size != rowSize * rowSize) return null
            // create an array with dummy values, which we will fill with real values
            val tileArray = Array(rowSize) { emptyArray<BingoTile>() }
            for (row in 0 until rowSize) {
                val rowArray = input.subList(row * rowSize, (row + 1) * rowSize).map { BingoTile(it, marked = false) }.toTypedArray()
                tileArray[row] = rowArray
            }
            return BingoBoard(tileArray)
        }
    }
}