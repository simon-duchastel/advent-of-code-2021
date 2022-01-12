package problems.problem2

/**
 * Given a list of submarine commands, returns the product of the final horizontal position
 * and the final depth.
 */
fun calculateCourse(commands: List<SubmarineCommand>): Int {
    return commands.fold(Pair(0, 0)) { (horizontal, depth), command ->
        when (command) {
            is SubmarineCommand.Forward -> Pair(horizontal + command.magnitude, depth)
            is SubmarineCommand.Down -> Pair(horizontal, depth + command.magnitude)
            is SubmarineCommand.Up -> Pair(horizontal, depth - command.magnitude)
        }
    }.let { (horizontal, depth) -> horizontal * depth }
}

/**
 * Given a list of submarine commands, returns the product of the final horizontal position
 * and the final depth using the aim calculation.
 */
fun calculateCourseWithAim(commands: List<SubmarineCommand>): Int {
    return commands.fold(Triple(0, 0, 0)) { (horizontal, depth, aim), command ->
        when (command) {
            is SubmarineCommand.Forward -> Triple(horizontal + command.magnitude, depth + (aim * command.magnitude), aim)
            is SubmarineCommand.Down -> Triple(horizontal, depth, aim + command.magnitude)
            is SubmarineCommand.Up -> Triple(horizontal, depth, aim - command.magnitude)
        }
    }.let { (horizontal, depth, _) -> horizontal * depth }
}

/**
 * Possible commands the submarine can follow
 */
sealed class SubmarineCommand(val magnitude: Int) {
    class Forward(magnitude: Int): SubmarineCommand(magnitude)
    class Down(magnitude: Int): SubmarineCommand(magnitude)
    class Up(magnitude: Int): SubmarineCommand(magnitude)

    companion object {
        /**
         * Parses this string as a SubmarineCommand, or returns null if the string is not a valid submarine command
         * Valid SubmarineCommands are:
         * 'FORWARD <integer>'
         * 'UP <integer>'
         * 'DOWN <integer>'
         */
        fun parseSubmarineCommand(input: String): SubmarineCommand? {
            val split = input.split(" ", limit = 2)
            val magnitude = split.getOrNull(1)?.toIntOrNull() ?: return null
            return when (split[0].toLowerCase()) {
                "forward" -> Forward(magnitude)
                "down" -> Down(magnitude)
                "up" -> Up(magnitude)
                else -> null
            }
        }
    }
}