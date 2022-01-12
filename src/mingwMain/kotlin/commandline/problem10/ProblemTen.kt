package commandline.problem10

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem1.findDepthIncreasesNWindow
import problems.problem1.findDepthIncreasesPair
import problems.problem10.SyntaxCharacter
import problems.problem10.findIllegalChunkScore
import problems.problem10.findIncompleteChunkScore

/**
 * Encapsulates all the command line code for problem ten.
 */
object ProblemTen {
    val action = RunnerAction("ten", "Run the tenth problem", ::problemTen)

    private fun problemTen() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem ten") {
        val symbolList = CommandLineRunner.askForFileInput("symbol",
            "Please provide a file with lines of symbols separated by newlines."
        ) { line -> line.map { char -> SyntaxCharacter.fromString(char) ?: return@askForFileInput null } }

        if (symbolList != null) {
            val answer = findIllegalChunkScore(symbolList)
            println("Score of illegal chunks is $answer.")
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem ten") {
        val symbolList = CommandLineRunner.askForFileInput("symbols",
            "Please provide a file with lines of symbols separated by newlines."
        ) { line -> line.map { char -> SyntaxCharacter.fromString(char) ?: return@askForFileInput null } }

        if (symbolList != null) {
            val answer = findIncompleteChunkScore(symbolList)
            println("Score of incomplete chunks is $answer.")
        }
    }
}