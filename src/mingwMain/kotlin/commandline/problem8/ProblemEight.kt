package commandline.problem8

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem8.*

/**
 * Encapsulates all the command line code for problem eight.
 */
object ProblemEight {
    val action = RunnerAction("eight", "Run the eighth problem", ::problemEigth)

    private fun problemEigth() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem eight") {
        val puzzle = retrieveSegmentInput()

        if (puzzle != null) {
            // combine all the outputs together to form the input to the function
            val answer = findNumOneFourSevenEight(puzzle.flatMap { it.output })
            println("The digits '1', '4', '7', and '8' appear in the output $answer times.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem eight") {
        val puzzle = retrieveSegmentInput()

        if (puzzle != null) {
            val answer = decodeAndSumSignals(puzzle)
            println("The sum of all decoded outputs is $answer.")
        }
    }

    /**
     * Asks the user for seven-segment signal inputs from I/O and returns it, or returns null (providing the user
     * with a helpful error) if there was a failure in doing so.
     */
    private fun retrieveSegmentInput(): List<SegmentSignalPuzzle>? {
        return CommandLineRunner.askForFileInput("seven-segment signal",
            "Please provide a file with seven-segment signals separated by newlines. A seven-segment signal " +
                    "is a series 10 seven-segments separated by spaces, followed by a '|', followed by 4 more seven-segments " +
                    " separated by spaces. A seven-segment is a series of up to seven letters a-g."
        ) { line ->
            val split = line.split(" | ")
            if (split.size != 2) return@askForFileInput null
            val uniquePatternsRaw = split[0]
            val outputRaw = split[1]

            val uniquePatterns = uniquePatternsRaw.split(' ').map { SegmentSignal.fromString(it) }
            val output = outputRaw.split(' ').map { SegmentSignal.fromString(it) }

            if (uniquePatterns.any { it == null } || output.any { it == null }) return@askForFileInput null
            SegmentSignalPuzzle(uniquePatterns = uniquePatterns.filterNotNull(), output = output.filterNotNull())
        }
    }
}