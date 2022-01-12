package commandline.problem12

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem12.constructCavern
import problems.problem12.findTotalPathsMultiVisit
import problems.problem12.findTotalPathsSingleVisit
import problems.problem12.RawCavernConnection

/**
 * Encapsulates all the command line code for problem twelve.
 */
object ProblemTwelve {
    val action = RunnerAction("twelve", "Run the twelfth problem", ::problemTwelve)

    private fun problemTwelve() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem twelve") {
        val cavernConnections = CommandLineRunner.askForFileInput("cavern",
            "Please provide a file with lines of cavern connections (representing 2 caverns connected together in a" +
                    "graph, in the form '<cavern1>-<cavern2>') separated by newlines."
        ) { line ->
            val split = line.split('-')
            if (split.size != 2) return@askForFileInput null
            RawCavernConnection(split[0] to split[1])
        }

        val startOfCavern = cavernConnections?.let(::constructCavern)
        if (startOfCavern != null) {
            val answer = findTotalPathsSingleVisit(startOfCavern)
            println("Total number of cavern paths (with no repeat visits to small caves) is $answer.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem twelve") {
        val cavernConnections = CommandLineRunner.askForFileInput("cavern",
            "Please provide a file with lines of cavern connections (representing 2 caverns connected together in a" +
                    "graph, in the form '<cavern1>-<cavern2>') separated by newlines."
        ) { line ->
            val split = line.split('-')
            if (split.size != 2) return@askForFileInput null
            RawCavernConnection(split[0] to split[1])
        }

        val startOfCavern = cavernConnections?.let(::constructCavern)
        if (startOfCavern != null) {
            val answer = findTotalPathsMultiVisit(startOfCavern)
            println("Total number of cavern paths (with 1 allowed repeat visit to a small cave) is $answer.")
        }
    }
}