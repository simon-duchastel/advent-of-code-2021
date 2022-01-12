package commandline.problem6

import commandline.runner.CommandLineRunner
import commandline.runner.RunnerAction
import problems.problem6.LanternFish
import problems.problem6.findTotalFishWithOptimizedSimulation

/**
 * Encapsulates all the command line code for problem six.
 */
object ProblemSix {
    val action = RunnerAction("six", "Run the sixth problem", ::problemSix)

    private fun problemSix() {
        val numDays = CommandLineRunner.askForCommandLineInput(
            "How many days?",
            "integer",
            "Please provide a valid integer value.") { it.toIntOrNull() }
        if (numDays != null) {
            val startingFish: List<LanternFish>? = CommandLineRunner.askForFileInputFold("lantern fish",
                "Please provide a file with lantern fish represented as integers separated by commas and newlines.",
                emptyList()
            ) { fish, line ->
                // Read the next line of fish, concat it to the current line, and if any are null return null out of the whole
                // thing (which will throw a UI error within the fold function).
                val lineOfFish = fish + line.split(',').map { LanternFish.fromString(it.trim()) }
                if (lineOfFish.any { it == null }) null else lineOfFish.mapNotNull { it }
            }

            if (startingFish != null) {
                val answer = findTotalFishWithOptimizedSimulation(startingFish, numDays)
                println("There are $answer LanternFish after $numDays days.")
            }
        }
    }
}