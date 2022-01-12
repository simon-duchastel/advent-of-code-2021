package commandline.problem13

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem13.Fold
import problems.problem13.performAllFolds
import problems.problem13.visibleDotsAfterNFolds

/**
 * Encapsulates all the command line code for problem thirteen.
 */
object ProblemThirteen {
    val action = RunnerAction("thirteen", "Run the thirteenth problem", ::problemThirteen)

    private fun problemThirteen() {
        print("Part one or two? ")
        CommandLineRunner.askForCommandInput(listOf(partOneAction, partTwoAction))
    }

    private val partOneAction = RunnerAction("one", "Run part one of problem thirteen") {
        val instructions = askForInstructionsFromInput()
        if (instructions != null) {
            val numFolds = CommandLineRunner.askForCommandLineInput(
                "How many folds?",
                "integer",
                "Please provide a valid integer value.") { it.toIntOrNull() }

            if (numFolds != null) {
                if (numFolds > instructions.folds.size) {
                    println("Insufficient number of folds provided. Got ${instructions.folds.size}, but you asked for $numFolds.")
                } else {
                    val answer = visibleDotsAfterNFolds(instructions.dots, instructions.folds, numFolds)
                    if (answer != null) {
                        println("Total number of dots visible after $numFolds folds is $answer.")
                    } else {
                        println("Unexpected error processing folds.")
                    }
                }
            }
        }
    }

    private val partTwoAction = RunnerAction("two", "Run part two of problem thirteen") {
        val instructions = askForInstructionsFromInput()
        if (instructions != null) {
            val answer = performAllFolds(instructions.dots, instructions.folds)
            if (answer != null) {
                println("Displaying final paper...")
                println(generateDotPaperString(answer) ?: "Error displaying final paper.")
            } else {
                println("Unexpected error processing folds.")
            }
        }
    }

    /**
     * Return a string representing the [dots] on the paper, or null if dots is empty.
     */
    private fun generateDotPaperString(dots: List<Pair<Int, Int>>): String? {
        if (dots.isEmpty()) return null

        // find the paper size, ie. the largets x value and the largest y
        val paperSize = (dots.maxBy { it.first }?.first ?: return null) to (dots.maxBy { it.second }?.second ?: return null)

        // for each spot in the paper, add a '#' if a dot exists there, otherwise add a ' '
        // (note this is a very inefficient brute force approach)
        var output = ""
        for (y in 0..paperSize.second) {
            for (x in 0..paperSize.first) { // append each dot in the row, then move to the next column
                output += if (dots.contains(x to y)) "#" else " "
            }
            output += "\n"
        }

        return output
    }

    /**
     * Asks the user for [Instructions] from the CommandLine, either returning an
     * Instructions object or null if an error occurred.
     */
    private fun askForInstructionsFromInput(): Instructions? {
        return CommandLineRunner.askForFileInputFold("instruction",
            "Please provide a file with lines of coordinates (representing dots on the instruction paper" +
                    "followed by lines of folds (in the form 'fold along <x|y>=<INT>'), alll separated by newlines.",
            Instructions(emptyList(), emptyList())
        ) { instructions, line ->
            when {
                line.isEmpty() -> instructions
                line.toLowerCase().startsWith("fold along ") -> {
                    val trimmed =  line.toLowerCase().trim().replace("fold along ", "") // should now be '<x|y>=<int>'
                    val split = trimmed.split("=", limit = 2) // split[0] = either x or y, split[1] = some integer
                    if (split.size != 2) return@askForFileInputFold null
                    val location = split[1].toIntOrNull() ?: return@askForFileInputFold null
                    val fold = when {
                        split[0] == "x" -> Fold.X(location)
                        split[0] == "y" -> Fold.Y(location)
                        else -> return@askForFileInputFold null
                    }
                    instructions.copy(folds = instructions.folds + fold)
                }
                line.toLowerCase().contains(",") -> {
                    val split = line.toLowerCase().split(",", limit = 2) // split[0] = some integer, split[1] = some integer
                    if (split.size != 2) return@askForFileInputFold null
                    val x = split[0].toIntOrNull() ?: return@askForFileInputFold null
                    val y = split[1].toIntOrNull() ?: return@askForFileInputFold null
                    instructions.copy(dots = instructions.dots + (x to y))
                }
                else -> return@askForFileInputFold null
            }
        }
    }

    /**
     * Represents an instruction paper with dots on it and a series of folds to perform
     * to the paper in order to get the final instructions.
     */
    data class Instructions(val dots: List<Pair<Int, Int>>, val folds: List<Fold>)
}