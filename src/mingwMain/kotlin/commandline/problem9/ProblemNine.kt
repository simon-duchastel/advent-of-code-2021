package commandline.problem9

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem9.findProductOfLargestBasins
import problems.problem9.findSingleRiskValueOfLowestLavaTubes

/**
 * Encapsulates all the command line code for problem nine.
 */
object ProblemNine {
    val action = RunnerAction("nine", "Run the ninth problem", ::problemNine)

    private fun problemNine() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem nine") {
        val lavaTubeMap = CommandLineRunner.askForFileInput("integer digits",
            "Please provide a file of integer digits separated by newlines."
        ) { line -> line.map { digit -> digit.toString().toIntOrNull() ?: return@askForFileInput null } }

        if (lavaTubeMap != null) {
            val answer = findSingleRiskValueOfLowestLavaTubes(lavaTubeMap)
            println("The risk value of the lowest lava tubes is $answer.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem nine") {
        val lavaTubeMap = CommandLineRunner.askForFileInput("integer digits",
            "Please provide a file of integer digits separated by newlines."
        ) { line -> line.map { digit -> digit.toString().toIntOrNull() ?: return@askForFileInput null } }

        if (lavaTubeMap != null) {
            val answer = findProductOfLargestBasins(lavaTubeMap, 3)
            println("The product of the 3 lowest lava basins is $answer.")
        }
    }
}