package problems.problem10

import utils.MutableStack

/**
 * Find the score by adding up all of the scores of each illegal chunk, ignoring successful and incomplete chunks.
 * Illegal chunks are those with non-matching closing symbols. Chunks are scored as follows:
 * - chunk is malformed due to ) -     3 points.
 * - chunk is malformed due to ] -    57 points.
 * - chunk is malformed due to } -  1197 points.
 * - chunk is malformed due to > -  25137 points.
 */
fun findIllegalChunkScore(lines: List<List<SyntaxCharacter>>): Int {
    return lines.fold(0) { score, line ->
        val symbolStack = MutableStack<SyntaxCharacter>()
        var invalidSymbol: SyntaxCharacter? = null
        loop@ for (symbol in line) {
            when (symbol) {
                SyntaxCharacter.LParen,
                SyntaxCharacter.LBracket,
                SyntaxCharacter.LSquiggly,
                SyntaxCharacter.LessThan -> symbolStack.push(symbol)
                SyntaxCharacter.RParen -> {
                    if (symbolStack.peek() is SyntaxCharacter.LParen) {
                        symbolStack.pop()
                    } else {
                        invalidSymbol = SyntaxCharacter.RParen
                        break@loop
                    }
                }
                SyntaxCharacter.RBracket -> {
                    if (symbolStack.peek() is SyntaxCharacter.LBracket) {
                        symbolStack.pop()
                    } else {
                        invalidSymbol = SyntaxCharacter.RBracket
                        break@loop
                    }
                }
                SyntaxCharacter.RSquiggly -> {
                    if (symbolStack.peek() is SyntaxCharacter.LSquiggly) {
                        symbolStack.pop()
                    } else {
                        invalidSymbol = SyntaxCharacter.RSquiggly
                        break@loop
                    }
                }
                SyntaxCharacter.GreaterThan -> {
                    if (symbolStack.peek() is SyntaxCharacter.LessThan) {
                        symbolStack.pop()
                    } else {
                        invalidSymbol = SyntaxCharacter.GreaterThan
                        break@loop
                    }
                }
            }
        }

        score + when (invalidSymbol) {
            null -> 0
            SyntaxCharacter.RParen -> 3
            SyntaxCharacter.RBracket -> 57
            SyntaxCharacter.RSquiggly -> 1197
            SyntaxCharacter.GreaterThan -> 25137
            else -> 0
        }
    }
}

/**
 * Find the score by taking the median score of all incomplete chunk scores, ignoring successful and illegal chunks.
 * Incomplete chunks are those with missing closing symbols at the end. Chunks are scored as follows:
 * - Each missing character multiplies the score by 5, then is added with:
 * - for a missing ) - 1 point.
 * - for a missing ] - 2 points.
 * - for a missing } - 3 points.
 * - for a missing > - 4 points.
 */
fun findIncompleteChunkScore(lines: List<List<SyntaxCharacter>>): Long {
    return lines.fold(emptyList<Long>()) { score, line -> // Use long since Int will cause the score to overflow
        val symbolStack = MutableStack<SyntaxCharacter>()
        loop@ for (symbol in line) {
            when (symbol) {
                SyntaxCharacter.LParen,
                SyntaxCharacter.LBracket,
                SyntaxCharacter.LSquiggly,
                SyntaxCharacter.LessThan -> symbolStack.push(symbol)
                SyntaxCharacter.RParen -> {
                    if (symbolStack.peek() is SyntaxCharacter.LParen) {
                        symbolStack.pop()
                    } else {
                        symbolStack.clear() // clear the stack as this is an illegal chunk
                        break@loop
                    }
                }
                SyntaxCharacter.RBracket -> {
                    if (symbolStack.peek() is SyntaxCharacter.LBracket) {
                        symbolStack.pop()
                    } else {
                        symbolStack.clear() // clear the stack as this is an illegal chunk
                        break@loop
                    }
                }
                SyntaxCharacter.RSquiggly -> {
                    if (symbolStack.peek() is SyntaxCharacter.LSquiggly) {
                        symbolStack.pop()
                    } else {
                        symbolStack.clear() // clear the stack as this is an illegal chunk
                        break@loop
                    }
                }
                SyntaxCharacter.GreaterThan -> {
                    if (symbolStack.peek() is SyntaxCharacter.LessThan) {
                        symbolStack.pop()
                    } else {
                        symbolStack.clear() // clear the stack as this is an illegal chunk
                        break@loop
                    }
                }
            }
        }

        var newScore = 0L
        while (symbolStack.peek() != null) {
            newScore *= 5
            newScore += when (symbolStack.pop()) {
                null -> 0 // should not occur due to peek() check
                SyntaxCharacter.LParen -> 1 // means a ) was missing
                SyntaxCharacter.LBracket -> 2 // means a ] was missing
                SyntaxCharacter.LSquiggly -> 3 // means a } was missing
                SyntaxCharacter.LessThan -> 4 // means a > was missing
                else -> 0 // should not occur - we only add (,[,{,< to the stack
            }
        }

        score + newScore
    }
        .filter { it > 0 } // don't include 0-scores (these are lines that were not incomplete, hence 0 score)
        .sorted().let { it[it.size / 2] } // get the median score, (the one in the very middle of the sorted list)
}

/**
 * Represents a syntax character.
 */
sealed class SyntaxCharacter {
    // (
    object LParen: SyntaxCharacter() { override fun toString(): String = "(" }
    // )
    object RParen: SyntaxCharacter() { override fun toString(): String = ")" }

    // [
    object LBracket: SyntaxCharacter() { override fun toString(): String = "[" }
    // ]
    object RBracket: SyntaxCharacter() { override fun toString(): String = "]" }

    // {
    object LSquiggly: SyntaxCharacter() { override fun toString(): String = "{" }
    // }
    object RSquiggly: SyntaxCharacter() { override fun toString(): String = "}" }

    // <
    object LessThan: SyntaxCharacter() { override fun toString(): String = "<" }
    // >
    object GreaterThan: SyntaxCharacter() { override fun toString(): String = ">" }

    companion object {
        /**
         * Converts [input] into a [SyntaxCharacter], or null if it is not a valid syntax character.
         */
        fun fromString(input: Char): SyntaxCharacter? {
            return when (input) {
                '(' -> LParen
                ')' -> RParen
                '[' -> LBracket
                ']' -> RBracket
                '{' -> LSquiggly
                '}' -> RSquiggly
                '<' -> LessThan
                '>' -> GreaterThan
                else -> null
            }
        }
    }
}