package commandline.problem5

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem5.LineSegment
import problems.problem5.findNumOverlappingPoints
import problems.problem5.findNumNonDiagonallyOverlappingPoints

/**
 * Encapsulates all the command line code for problem five.
 */
object ProblemFive {
    val action = RunnerAction("five", "Run the fifth problem", ::problemFive)

    private fun problemFive() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem five") {
        val lineSegmentList = CommandLineRunner.askForFileInput("line segments",
            "Please provide a file with line segments separated by newlines."
        ) { LineSegment.fromString(it) }

        if (lineSegmentList != null) {
            val answer = findNumNonDiagonallyOverlappingPoints(lineSegmentList)
            println("Line segments overlap $answer times. This only includes horizontal and vertical lines, not diagonals.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem five") {
        val lineSegmentList = CommandLineRunner.askForFileInput("line segment",
            "Please provide a file with line segments separated by newlines."
        ) { LineSegment.fromString(it) }

        if (lineSegmentList != null) {
            val answer = findNumOverlappingPoints(lineSegmentList)
            println("Line segments overlap $answer times. This includes horizontal, vertical, and 45 degree diagonal lines.")
        }
    }
}