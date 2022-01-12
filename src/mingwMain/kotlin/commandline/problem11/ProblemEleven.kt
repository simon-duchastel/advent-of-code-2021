package commandline.problem11

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem11.findFirstSimultaneousFlash
import problems.problem11.Octopus
import problems.problem11.OctopusCavern
import problems.problem11.simulateOctopusSteps

/**
 * Encapsulates all the command line code for problem twelve.
 */
object ProblemEleven {
    val action = RunnerAction("eleven", "Run the eleventh problem", ::problemEleven)

    private fun problemEleven() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem eleven") {
        val numSteps = CommandLineRunner.askForCommandLineInput(
            "How many steps?",
            "integer",
            "Please provide a valid integer value.") { it.toIntOrNull() }
        if (numSteps != null) {
            val octopusMap = CommandLineRunner.askForFileInput("octopus",
                "Please provide a file with lines of integer digits (representing octopi) separated by newlines."
            ) { line ->
                line.map {
                        digit -> digit.toString().toIntOrNull()?.let { Octopus(it) } ?: return@askForFileInput null
                }
            }

            if (octopusMap != null) {
                val cavern = OctopusCavern(octopusMap)
                val answer = simulateOctopusSteps(n = numSteps, initialCavern = cavern).totalFlashes
                println("Total number of flashes is $answer.")
            }
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem eleven") {
        val octopusMap = CommandLineRunner.askForFileInput("octopus",
            "Please provide a file with lines of integer digits (representing octopi) separated by newlines."
        ) { line ->
            line.map {
                    digit -> digit.toString().toIntOrNull()?.let { Octopus(it) } ?: return@askForFileInput null
            }
        }

        if (octopusMap != null) {
            val cavern = OctopusCavern(octopusMap)
            val answer = findFirstSimultaneousFlash(initialCavern = cavern)
            println("First step with all simulatenous flashes is $answer.")
        }
    }
}