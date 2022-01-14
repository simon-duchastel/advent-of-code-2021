package commandline.runner

import commandline.problem1.ProblemOne
import commandline.problem2.ProblemTwo
import commandline.problem3.ProblemThree
import commandline.problem4.ProblemFour
import commandline.problem5.ProblemFive
import commandline.problem6.ProblemSix
import commandline.problem7.ProblemSeven
import commandline.problem8.ProblemEight
import commandline.problem9.ProblemNine
import commandline.problem10.ProblemTen
import commandline.problem11.ProblemEleven
import commandline.problem12.ProblemTwelve
import commandline.problem13.ProblemThirteen
import commandline.problem14.ProblemFourteen

/**
 * Guides the user through a flow on the command line to figure out which problem they
 * would like to run
 */
fun main() {
    print("Which problem would you like to run? ")
    CommandLineRunner.askForCommandInput(listOf(
        ProblemOne.action,
        ProblemTwo.action,
        ProblemThree.action,
        ProblemFour.action,
        ProblemFive.action,
        ProblemSix.action,
        ProblemSeven.action,
        ProblemEight.action,
        ProblemNine.action,
        ProblemTen.action,
        ProblemEleven.action,
        ProblemTwelve.action,
        ProblemThirteen.action,
        ProblemFourteen.action
    ))
}

object CommandLineRunner {
    /**
     * The help action allowing the user to request help.
     */
    private val helpAction = helpAction(emptyList())
    private fun helpAction(possibleActions: List<RunnerAction>) = RunnerAction("help", "Prints this help text") { printHelp(possibleActions) }

    /**
     * Prints help text to standard output
     */
    private fun printHelp(possibleActions: List<RunnerAction> = listOf()) {
        println("============================================")
        println("Simon Duchastel's 2021 Advent of Code Runner")
        println()
        println("==========")
        println("Help guide")
        println("==========")
        println()
        println("Follow the prompts on screen to run your selected AoC problem.")
        println("Possible commands")
        for (action in (possibleActions + helpAction)) {
            println("\t${action.command} - ${action.description}")
        }
    }

    /**
     * Asks the user for input on the next action to execute. Takes as input a set of [actions] which constitute
     * valid inputs from the user and corresponding actions for this runner to take.
     */
    fun askForCommandInput(actions: List<RunnerAction>) {
        // Generate a mapping of input commands to actions given a list of actions
        val actionMap = actions.fold(mapOf(Pair("help", helpAction(actions)))) { map, action ->
            if (action.command !in map) {
                map + Pair(action.command, action)
            } else{
                map
            }
        }

        // Read the user's input and execute the appropriate action, or print the help text if no valid input found
        val input = readLine()?.toLowerCase()
        if (input in actionMap) {
            actionMap[input]!!() // we can safely use !! since we know the immutable map contains the input
        } else {
            println("Unrecognized input. Please input a valid command.")
            printHelp(actions)
        }
    }

    /**
     * Ask the user for a value of type [valueName] on the command line by asking [instructionText].
     * If the user does not provide a value value, print an error and return null.
     * [mapper] specifies a function for mapping the user's String input value to the value of type [I], or null if mapping is not possible.
     * In the case that a mapping is not possible, [errorText] will be displayed.
     */
    fun <I> askForCommandLineInput(instructionText: String, valueName: String, errorText: String, mapper: (inputLine: String) -> I?): I? {
        print("$instructionText ")
        val input = readLine()
        if (input == null) {
            println("No $valueName input found. $errorText")
            return null // return null from the entire function since no input was provided
        }
        val mappedValue = mapper(input)
        return if (mappedValue == null) {
            println("\"$input\" is not a valid $valueName. $errorText")
            null // return null from the entire function since we could not map
        } else {
            mappedValue
        }
    }

    /**
     * Ask the user for a Sequence of values of type [valueName], provided as a path to a file containing a list of
     * line-separated values. If no list of values can be provided, return null.
     * [mapper] specifies a function for mapping input Strings in the file to the values of type [I] or null if mapping is not possible.
     * In the case that a mapping is not possible, [errorText] will be displayed.
     */
    fun <I> askForFileInput(valueName: String, errorText: String, mapper: (inputLine: String) -> I?): List<I>? {
        print("What is your input? (Please provide the path to a file containing ${valueName}s, separated by newlines). ")
        val pathToFile = readLine()
        if (pathToFile == helpAction.command) helpAction()
        if (pathToFile.isNullOrBlank()) {
            println("Invalid file path. Please provide a valid file path.")
            return null
        }

        // Collect the sequence of Strings from the file and map them into values of type I, or return null with a message if
        // unable to.
        // Note that because we're going from a Sequence to a List, this operation involves I/O under the hood.
        return FileReader.readFromFile(pathToFile)?.toList()?.map { it.trim() }?.map {
            val mappedValue = mapper(it)
            if (mappedValue == null) {
                println("\"$it\" is not a valid $valueName. $errorText")
                return null // return null from the entire function
            } else {
                mappedValue
            }
        }
    }

    /**
     * Ask the user for a Sequence of values of type [valueName], provided as a path to a file containing a list of
     * line-separated values. If no list of values can be provided, return null.
     * [mapper] specifies a function for folding input Strings in the file to the values of type [I], or null if mapping is not possible.
     * [initial] specifies the initial value to use to begin the fold operation.
     * In the case that a folding is not possible, [errorText] will be displayed.
     */
    fun <I> askForFileInputFold(valueName: String, errorText: String, initial: I, mapper: (acc: I, inputLine: String) -> I?): I? {
        print("What is your input? (Please provide the path to a file containing ${valueName}s, separated by newlines). ")
        val pathToFile = readLine()
        if (pathToFile == helpAction.command) helpAction()
        if (pathToFile.isNullOrBlank()) {
            println("Invalid file path. Please provide a valid file path.")
            return null
        }

        // Collect the sequence of Strings from the file and fold them into values of type I, or return null with a message if
        // unable to.
        // Note that because we're going from a Sequence to a List, this operation involves I/O under the hood.
        return FileReader.readFromFile(pathToFile)?.toList()?.map { it.trim() }?.fold(initial) { fileAcc, fileLine ->
            val mappedValue = mapper(fileAcc, fileLine)
            if (mappedValue == null) {
                println("\"$fileLine\" does not conform to a valid $valueName. $errorText")
                return null // return null from the entire function
            } else {
                mappedValue
            }
        }
    }
}



/**
 * Represents an action that can be run on the command line
 */
data class RunnerAction(
    /**
     * The command inputted by the user on the command line to run this action
     */
    val command: String,

    /**
     * A concise description of this action for display to the user
     */
    val description: String,

    /**
     * The lambda executed by the program when the user decides to run the action
     */
    val action: () -> Unit
) {
    operator fun invoke() = action() // convenience function to allow doing `runnerAction()` rather than `runnerAction.action()`
}

