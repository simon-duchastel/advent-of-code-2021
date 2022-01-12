package commandline.problem1

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem1.findDepthIncreasesNWindow
import problems.problem1.findDepthIncreasesPair

/**
 * Encapsulates all the command line code for problem one.
 */
object ProblemOne {
    val action = RunnerAction("one", "Run the first problem", ::problemOne)

    private fun problemOne() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem one") {
        val intList = CommandLineRunner.askForFileInput("integer",
            "Please provide a file with integers separated by newlines."
        ) { it.toIntOrNull() }

        if (intList != null) {
            val answer = findDepthIncreasesPair(intList)
            println("Depths increase $answer times.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem one") {
        val intList = CommandLineRunner.askForFileInput("integer",
            "Please provide a file with integers separated by newlines."
        ) { it.toIntOrNull() }

        if (intList != null) {
            val answer = findDepthIncreasesNWindow(intList, 3)
            println("Depths (with sliding window) increase $answer times.")
        }
    }
}