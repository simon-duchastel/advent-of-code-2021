package commandline.problem2

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem2.SubmarineCommand
import problems.problem2.calculateCourse
import problems.problem2.calculateCourseWithAim

/**
 * Encapsulates all the command line code for problem two.
 */
object ProblemTwo {
    val action = RunnerAction("two", "Run the second problem", ::problemTwo)

    private fun problemTwo() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem two") {
        val commandsList = CommandLineRunner.askForFileInput("submarine command",
            "Please provide a file with submarine commands ('FORWARD <integer>'/'UP <integer>'/'DOWN <integer>', separated by newlines."
        ) { SubmarineCommand.parseSubmarineCommand(it) }

        if (commandsList != null) {
            val answer = calculateCourse(commandsList)
            println("Product of final horizontal position and final depth is $answer.")
        }
    }

     private val partTwoAction = RunnerAction("two", "Run part two of problem two") {
        val commandsList = CommandLineRunner.askForFileInput("submarine command",
            "Please provide a file with submarine commands ('FORWARD <integer>'/'UP <integer>'/'DOWN <integer>', separated by newlines."
        ) { SubmarineCommand.parseSubmarineCommand(it) }

        if (commandsList != null) {
            val answer = calculateCourseWithAim(commandsList)
            println("Product of final horizontal position and final depth (with aim) is $answer.")
        }
    }
}