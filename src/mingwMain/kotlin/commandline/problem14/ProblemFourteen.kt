package commandline.problem14

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem14.findDifferenceInElementsSimulation
import problems.problem14.findDifferenceInElementsBruteForce
import problems.problem14.InsertionRule
import problems.problem14.Polymer

/**
 * Encapsulates all the command line code for problem fourteen.
 */
object ProblemFourteen {
    val action = RunnerAction("fourteen", "Run the fourteenth problem", ::problemThirteen)

    private fun problemThirteen() {
        print("Efficient or BruteForce algorithm? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("efficient", "Run part one of problem fourteen") {
        val insertions = askForInsertionsFromCommandLine()
        if (insertions != null) {
            val numInsertions = CommandLineRunner.askForCommandLineInput(
                "How many insertions?",
                "integer",
                "Please provide a valid integer value.") { it.toIntOrNull() }

            if (numInsertions != null) {
                val answer = findDifferenceInElementsSimulation(insertions.polymer, insertions.insertionRules, numInsertions)
                if (answer != null) {
                    println("The difference in the polymer segment counts after $numInsertions insertions is $answer.")
                } else {
                    println("Invalid input - polymer was empty during evaluation.")
                }
            }
        }
    }

    private val partTwoAction = RunnerAction("bruteforce", "Run part two of problem fourteen") {
        val insertions = askForInsertionsFromCommandLine()
        if (insertions != null) {
            val numInsertions = CommandLineRunner.askForCommandLineInput(
                "How many insertions?",
                "integer",
                "Please provide a valid integer value.") { it.toIntOrNull() }

            if (numInsertions != null) {
                val answer = findDifferenceInElementsBruteForce(insertions.polymer, insertions.insertionRules, numInsertions)
                if (answer != null) {
                    println("The difference in the polymer segment counts after $numInsertions insertions is $answer.")
                } else {
                    println("Invalid input - polymer was empty during evaluation.")
                }
            }
        }
    }

    /**
     * Asks the user for an InsertionState of a starting polymer and insertion rules, provided
     * via the command line. Returns null if there was an error retrieving the user input.
     */
    private fun askForInsertionsFromCommandLine(): InsertionState? {
        return CommandLineRunner.askForFileInputFold("insertion rule",
            "Please provide a file with a starting polymer (a list of characters) followed by a list of insertion rules" +
                    "(in the form '## -> #', where each # is a character - although not necessarily the same character).",
            InsertionState(emptyList(), emptyList())
        ) { state, line ->
            when {
                line.isEmpty() -> state // ignore empty lines
                line.contains(" -> ") -> {
                    val split = line.split(" -> ", limit = 2) // split[0] = ##, split[1] = #, for # = some character
                    if (split.size != 2) return@askForFileInputFold null
                    if (!(Regex("[A-Za-z]{2}") matches split[0]) || !(Regex("[A-Za-z]") matches split[1])) return@askForFileInputFold null
                    state.copy(
                        insertionRules = state.insertionRules + InsertionRule(
                            split[0][0] to split[0][1],
                            split[1][0]
                        )
                    )
                }
                Regex("[A-Za-z]+") matches line -> { // this is the starting polymer
                    state.copy(polymer = line.toList())
                }
                else -> return@askForFileInputFold null // invalid state, input must be invalid
            }
        }
    }

    /**
     * Represents the state of a given [polymer] and its [insertionRules].
     */
    data class InsertionState(val polymer: Polymer, val insertionRules: List<InsertionRule>)
}