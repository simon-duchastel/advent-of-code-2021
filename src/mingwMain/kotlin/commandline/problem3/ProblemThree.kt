package commandline.problem3

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem3.Binary
import problems.problem3.calculateBinaryLifeSupportRating
import problems.problem3.calculateBinaryProduct

/**
 * Encapsulates all the command line code for problem three.
 */
object ProblemThree {
    val action = RunnerAction("three", "Run the third problem", ::problemThree)

    private fun problemThree() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))

    }

    private val partOneAction = RunnerAction("one", "Run part one of problem three") {
        val binaryList = CommandLineRunner.askForFileInput("binary string",
            "Please provide a file with binary strings separated by newlines."
        ) { Binary.fromString(it) }

        if (binaryList != null) {
            val answer = calculateBinaryProduct(binaryList)
            println("The power consumption (product of the binary gamma and epsilon values) is $answer.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem three") {
        val binaryList = CommandLineRunner.askForFileInput("binary string",
            "Please provide a file with binary strings separated by newlines."
        ) { Binary.fromString(it) }

        if (binaryList != null) {
            val answer = calculateBinaryLifeSupportRating(binaryList)
            println("The life support rating is $answer.")
        }
    }
}