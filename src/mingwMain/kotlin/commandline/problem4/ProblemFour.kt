package commandline.problem4

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem4.BingoBoard
import problems.problem4.findBingoWinningScore
import problems.problem4.findLastWinningBingoBoard

/**
 * Encapsulates all the command line code for problem four.
 */
object ProblemFour {
    val action = RunnerAction("four", "Run the fourth problem", ::problemFour)

    private fun problemFour() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem four") {
        val bingoInput = readBingoBoardsFromInput()
        if (bingoInput != null) {
            // Assume that we always generate a list of valid numbers to mark, even though our BingoInput allows
            // for it to be null (due to starting out with no numbers to mark)
            val answer = findBingoWinningScore(bingoInput.numbersToMark!!, bingoInput.boards)
            println("The winning board has a score of $answer.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem four") {
        val bingoInput = readBingoBoardsFromInput()
        if (bingoInput != null) {
            // Assume that we always generate a list of valid numbers to mark, even though our BingoInput allows
            // for it to be null (due to starting out with no numbers to mark)
            val answer = findLastWinningBingoBoard(bingoInput.numbersToMark!!, bingoInput.boards)
            println("The last winning board has a score of $answer.")
        }
    }

    /**
     * Read Bingo Boards from input, including asking the user for input on the command line. Returns null if no
     * input can be found.
     */
    private fun readBingoBoardsFromInput(): BingoInput? {
        return CommandLineRunner.askForFileInputFold(
            "bingo board input",
            "Please provide a file with valid bingo board inputs. This is a list of numbers to call followed by " +
                    "bingo boards, each separated by a newline. The list of numbers to call must be comma-separated on a" +
                    "single line. Each board is a list of numbers separated by commas, with each row separated by newlines.",
            initial = BingoInput(null, emptyList(), emptyList())
        ) { acc, inputRow ->
            when {
                // Assume the first row are the numbers to mark
                acc.numbersToMark == null -> {
                    val validNumbers = inputRow.split(',').map { it.trim().toIntOrNull() }
                    if (validNumbers.any { it == null }) {
                        null
                    } else {
                        // filter out the nulls to make the compiler happy (we already checked none exist)
                        acc.copy(numbersToMark = validNumbers.filterNotNull())
                    }
                }

                // Skip any empty lines
                inputRow.isBlank() -> acc

                // Assume this row belongs to a partial boards (or now-complete) board
                else -> {
                    // Accumulate all of this line's numbers into a typed list, checking for invariants
                    val newPartialList = inputRow.split(' ')
                        .filter { it.isNotBlank()  } // filter out blanks due to double-spaces
                        .map { it.trim().toIntOrNull() }

                    if (newPartialList.any { it == null } || newPartialList.size != BINGO_ROW_SIZE) {
                        null // return null if the line doesn't match our invariants
                    } else {
                        // filter out the nulls to make the compiler happy (we already checked none exist)
                        val accumulatedPartialBoard = acc.partialBoard.plus(newPartialList.filterNotNull())

                        // If the partial board is complete, create a new board
                        if (accumulatedPartialBoard.size == BINGO_ROW_SIZE * BINGO_ROW_SIZE) {
                            val newBoard = BingoBoard.createNewBoard(accumulatedPartialBoard, BINGO_ROW_SIZE)
                            if (newBoard != null) {
                                acc.copy(
                                    boards = acc.boards.plus(newBoard),
                                    partialBoard = emptyList()
                                )
                            } else {
                                null // we were not able to parse a valid board (it did not meet our invariants)
                            }
                        } else {
                            acc.copy(partialBoard = accumulatedPartialBoard)
                        }
                    }
                }
            }
        }
    }

    /**
     * Represents the input from a file of the Bingo Boards and numbers to mark.
     */
    data class BingoInput(val numbersToMark: List<Int>?, val boards: List<BingoBoard>, val partialBoard: List<Int>) {
        fun asString(): String {
            return (numbersToMark?.joinToString(separator = " ") { it.toString() } ?: "") + "\n" +
                    boards.joinToString("\n") { it.asString() } + "\n" +
                    partialBoard.joinToString(separator = " ") { it.toString() }
        }
    }

    private const val BINGO_ROW_SIZE = 5
}