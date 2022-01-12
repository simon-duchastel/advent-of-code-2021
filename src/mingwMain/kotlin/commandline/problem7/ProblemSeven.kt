package commandline.problem7

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem7.Crab
import problems.problem7.findOptimalCrabPositionExponential
import problems.problem7.findOptimalCrabPositionLinear

/**
 * Encapsulates all the command line code for problem seven.
 */
object ProblemSeven {
    val action = RunnerAction("seven", "Run the seventh problem", ::problemSeven)

    private fun problemSeven() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem seven") {
        val crabs = readCrabInput()
        if (crabs != null) {
            val answer = findOptimalCrabPositionLinear(crabs)
            println("$answer fuel needed to reach optimal gathering position given linear fuel cost.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem seven") {
        val crabs = readCrabInput()
        if (crabs != null) {
            val answer = findOptimalCrabPositionExponential(crabs)
            println("$answer fuel needed to reach optimal gathering position given exponential fuel cost.")
        }
    }

    /**
     * Read the list of crab positions from the user (prompting them to provide the input in file form). Return the
     * list of parsed crab positions, or null if it cannot be provided.
     */
    private fun readCrabInput(): List<Crab>? {
        return CommandLineRunner.askForFileInputFold("crab",
            "Please provide a file with crabs represented as integers separated by commas and newlines.",
            emptyList()
        ) { crabs, line ->
            // Read the next line of crabs, concat it to the current line, and if any are null return null out of the whole
            // thing (which will throw a UI error within the fold function).
            val lineOfCrabs = crabs + line.split(',').map { Crab.fromString(it.trim()) }
            if (lineOfCrabs.any { it == null }) null else lineOfCrabs.mapNotNull { it }
        }
    }
}