package problems.problem8

/**
 * Find the number of times the digits '1', '4', '7', or '8' appear in the
 * list of segment signals.
 */
fun findNumOneFourSevenEight(input: List<SegmentSignal>): Int {
    return input.count { it.isDigitOne() || it.isDigitFour() || it.isDigitSeven() || it.isDigitEight() }
}

/**
 * Given a list of [SegmentSignalPuzzle], decode the seven-segment displays and sum their outputs.
 * Each puzzles consists of a list of unique patterns which will help decipher the correct
 * segment-mappings for the corresponding output (itself a list of segment signals, which form
 * an integer display value).
 * Note that for this algorithm to work, each [SegmentSignalPuzzle] in [puzzles] must contain
 * one and only one [SegmentSignal] for each possible displayable digit, from 0-9. Returns null
 * if this is not the case is broken.
 */
fun decodeAndSumSignals(puzzles: List<SegmentSignalPuzzle>): Int? {
    // Run through every puzzles, decoding it and adding its output to the sum
    return puzzles.fold(0) { sum, puzzle ->
        val encodedOne = puzzle.uniquePatterns.find { it.isDigitOne() } ?: return null
        val encodedFour = puzzle.uniquePatterns.find { it.isDigitFour() } ?: return null
        val encodedSeven = puzzle.uniquePatterns.find { it.isDigitSeven() } ?: return null
        val encodedEight = puzzle.uniquePatterns.find { it.isDigitEight() } ?: return null

        // only 2, 3, and 5 have 5 segments and of those only 3 contains all the segments from 1
        val encodedThree = puzzle.uniquePatterns.filter { it.segments.size == 5 }.find { it.segments.containsAll(encodedOne.segments) } ?: return null

        // out of 3 and 5 which remain with 5 segments, only 5 matches the segments remaining from 4 after removing the segments from 1
        val encodedFive = puzzle.uniquePatterns
            .filter { it.segments.size == 5 }
            .find { it != encodedThree && it.segments.containsAll(encodedFour.segments - encodedOne.segments) } ?: return null

        // now that we know 3 and 5, 2 is the only remaining 5-segment digit
        val encodedTwo = puzzle.uniquePatterns.filter { it.segments.size == 5 }.find { it != encodedThree && it != encodedFive } ?: return null

        // only 0, 6, and 9 have 6 segments and of those only 0 doesn't match all the segments from 5pr
        val encodedZero = puzzle.uniquePatterns.filter { it.segments.size == 6 }.find { !it.segments.containsAll(encodedFive.segments) } ?: return null

        // out of 6 and 9 remaining with 6 segments, only 6 doesn't match all the segments of 1
        val encodedSix = puzzle.uniquePatterns.filter { it.segments.size == 6 }.find { !it.segments.containsAll(encodedOne.segments) } ?: return null

        // now that wee know 0 and 6, 9 is the only remaining 6-segment digit
        val encodedNine = puzzle.uniquePatterns.filter { it.segments.size == 6 }.find { it != encodedZero && it != encodedSix } ?: return null

        val decodedValue = puzzle.output.map {
            when (it) {
                encodedZero -> SegmentSignal.ZERO
                encodedOne -> SegmentSignal.ONE
                encodedTwo -> SegmentSignal.TWO
                encodedThree -> SegmentSignal.THREE
                encodedFour -> SegmentSignal.FOUR
                encodedFive -> SegmentSignal.FIVE
                encodedSix -> SegmentSignal.SIX
                encodedSeven -> SegmentSignal.SEVEN
                encodedEight -> SegmentSignal.EIGHT
                encodedNine -> SegmentSignal.NINE
                else -> return null // output does not correspond to a valid decoded signal
            }
        }.toInt() ?: return null // unable to decode value

        sum + decodedValue
    }
}

/**
 * Given a list of [SegmentSignalPuzzle], decode the seven-segment displays and sum their outputs.
 * Each puzzles consists of a list of unique patterns which will help decipher the correct
 * segment-mappings for the corresponding output (itself a list of segment signals, which form
 * an integer display value).
 * Note that this algorithm, although theoretically correct, will only work when given very large
 * numbers of SegmentSignal inputs to help it decode. Otherwise, it will not be able to decode and will
 * return null.
 *
 * Consider using [decodeAndSumSignals] instead.
 */
fun decodeAndSumSignalsHugeInput(puzzles: List<SegmentSignalPuzzle>): Int? {
    // Run through every puzzles, decoding it and adding its output to the sum
    return puzzles.fold(0) { sum, puzzle ->
        val allPossibleMappings = mapOf(
            SegmentSignal.Segment.A to SegmentSignal.ALL,
            SegmentSignal.Segment.B to SegmentSignal.ALL,
            SegmentSignal.Segment.C to SegmentSignal.ALL,
            SegmentSignal.Segment.D to SegmentSignal.ALL,
            SegmentSignal.Segment.E to SegmentSignal.ALL,
            SegmentSignal.Segment.F to SegmentSignal.ALL,
            SegmentSignal.Segment.G to SegmentSignal.ALL
        )

        // Start with a mapping of all segments to all other possible segments. For every pattern, whittle down
        // the set of possible segments until only valid mappings remain.
        val finalMapping = puzzle.uniquePatterns.fold(allPossibleMappings) { possibleMappings, pattern ->
            val newMapping = possibleMappings.toMutableMap() // find thew new possible mappings for the segments in this pattern
            when (pattern.segments.size) {
                2 -> { // This means it must be a 1, since that's the only digit 2 segments long
                    val invalidMappings = SegmentSignal.ALL.segments - SegmentSignal.ONE.segments
                    pattern.segments.forEach { curSegment ->
                        val newPossibleValues = possibleMappings.getValue(curSegment).segments.minus(invalidMappings)
                        newMapping[curSegment] = SegmentSignal(newPossibleValues)
                    }
                    newMapping
                }
                3 -> { // This means it must be a 7, since that's the only digit 3 segments long
                    val invalidMappings = SegmentSignal.ALL.segments - SegmentSignal.SEVEN.segments
                    pattern.segments.forEach { curSegment ->
                        val newPossibleValues = possibleMappings.getValue(curSegment).segments.minus(invalidMappings)
                        newMapping[curSegment] = SegmentSignal(newPossibleValues)
                    }
                    newMapping
                }
                4 -> { // This means it must be a 4, since that's the only digit 4 segments long
                    val invalidMappings = SegmentSignal.ALL.segments - SegmentSignal.FOUR.segments
                    pattern.segments.forEach { curSegment ->
                        val newPossibleValues = possibleMappings.getValue(curSegment).segments.minus(invalidMappings)
                        newMapping[curSegment] = SegmentSignal(newPossibleValues)
                    }
                    newMapping
                }
                5 -> { // This means it could be either a 2, 3, or 5 since they're all 5 segments long
                    val possibleSegments = SegmentSignal.TWO.segments + SegmentSignal.THREE.segments + SegmentSignal.FIVE.segments
                    val invalidMappings = SegmentSignal.ALL.segments - possibleSegments
                    pattern.segments.forEach { curSegment ->
                        val newPossibleValues = possibleMappings.getValue(curSegment).segments.minus(invalidMappings)
                        newMapping[curSegment] = SegmentSignal(newPossibleValues)
                    }
                    newMapping
                }
                6 -> { // This means it could be either a 0, 6, or 9 since they're all 6 segments long
                    val possibleSegments = SegmentSignal.ZERO.segments + SegmentSignal.SIX.segments + SegmentSignal.NINE.segments
                    val invalidMappings = SegmentSignal.ALL.segments - possibleSegments
                    pattern.segments.forEach { curSegment ->
                        val newPossibleValues = possibleMappings.getValue(curSegment).segments.minus(invalidMappings)
                        newMapping[curSegment] = SegmentSignal(newPossibleValues)
                    }
                    newMapping
                }
                7 -> { // This means it must be an 8, since that's the only digit 7 segments long
                    possibleMappings // the digit 8 is a no-op since it doesn't tell us anything new - any segments could go anywhere
                }
                else -> return null // Invalid segment length
            }
        }

        // Map our final mapping found from process of elimination into a DecodingMapping, or return null if we weren't
        // able to.
        val decodingMapping: DecodingMapping = finalMapping.mapValues { (_, possibleSegments) ->
            possibleSegments.segments.singleOrNull() ?: return null // If there's not exactly 1 possible segment, return null
        }

        // Use !! because if our decodingMapping file ends up incomplete, our algorithm is wrong and we should blow up.
        if (!decodingMapping.isComplete()) return null // we were unable to find a valid decoding
        val decodedValue = puzzle.output.map { it.decode(decodingMapping)!! }.toInt() ?: return null // unable to decode value
        sum + decodedValue
    }
}

/**
 * Regardless of whether this [SegmentSignal] is decoded or not, if it
 * has 2 segments it must be a '1' because only that digit uses 2 segments.
 */
fun SegmentSignal.isDigitOne(): Boolean = segments.size == 2

/**
 * Regardless of whether this [SegmentSignal] is decoded or not, if it
 * has 4 segments it must be a '4' because only that digit uses 4 segments.
 */
fun SegmentSignal.isDigitFour(): Boolean = segments.size == 4

/**
 * Regardless of whether this [SegmentSignal] is decoded or not, if it
 * has 3 segments it must be a '7' because only that digit uses 3 segments.
 */
fun SegmentSignal.isDigitSeven(): Boolean = segments.size == 3

/**
 * Regardless of whether this [SegmentSignal] is decoded or not, if it
 * has 7 segments it must be an '8' because only that digit uses 7 segments.
 */
fun SegmentSignal.isDigitEight(): Boolean = segments.size == 7

/**
 * Returns the [Int] representation of this seven-segment display, where
 * each element in the list is a seven-segment digit represented by a [SegmentSignal].
 * The first element in the list is the left-most (and thus most significant) digit,
 * and the last element the right-most (and thus least significant) digit.
 * Note that the underlying [SegmentSignal] objects must be decoded, otherwise this
 * output may be pure nonsense!
 * Returns null if any [SegmentSignal] in this list does not correspond to a value integer
 * digit, ie. it was not properly decoded.
 */
fun List<SegmentSignal>.toInt(): Int? {
    return this.foldRight(1 /* value to multiply */ to 0 /* value to sum */) { signal, (product, sum) ->
        val newProduct = product * 10 // new product goes up an order of magnitude
        val signalAsInt = signal.toInt() ?: return null
        val newSum = sum + signalAsInt * product // new sum is current signal as a digit multiplied by the product (to account for position) plus the old sum

        newProduct to newSum
    }.second // return the sum portion of the fold operation
}

/**
 * Represents a signal for lighting up a seven-segment display, composed
 * of a list of [Segment]s to light up.
 */
data class SegmentSignal(val segments: Set<Segment>) {

    /**
     * Provided with a [mapping] demonstrating what each incorrect [Segment] in this
     * [SegmentSignal] actually corresponds to, returns a new [SegmentSignal] that
     * Returns null if the [mapping] is incomplete, meaning we cannot decode.
     */
    fun decode(mapping: Map<Segment, Segment>): SegmentSignal? {
        return SegmentSignal(segments.map { mapping[it] ?: return null }.toSet())
    }

    /**
     * Returns the [Int] representation of this SegmentSignal.
     * Assumes that the SegmentSignal is decoded - otherwise, this
     * output may be pure nonsense!
     * Returns null if this [SegmentSignal] is not a valid signal, ie.
     * it was not properly decoded.
     */
    fun toInt(): Int? {
        return when {
            this == ZERO -> 0
            isDigitOne() -> 1
            this == TWO -> 2
            this == THREE -> 3
            isDigitFour() -> 4
            this == FIVE -> 5
            this == SIX -> 6
            isDigitSeven() -> 7
            isDigitEight() -> 8
            this == NINE -> 9
            else -> null
        }
    }

    /**
     * Represents a segment in a seven-segment display.
     */
    sealed class Segment {
        object A: Segment() {
            override fun toString(): String {
                return "A"
            }
        }
        object B: Segment() {
            override fun toString(): String {
                return "B"
            }
        }
        object C: Segment() {
            override fun toString(): String {
                return "C"
            }
        }
        object D: Segment() {
            override fun toString(): String {
                return "D"
            }
        }
        object E: Segment() {
            override fun toString(): String {
                return "E"
            }
        }
        object F: Segment() {
            override fun toString(): String {
                return "F"
            }
        }
        object G: Segment() {
            override fun toString(): String {
                return "G"
            }
        }

        companion object {
            /**
             * Takes a string as input and returns the corresponding Segment,
             * or null if it cannot be converted to a [Segment].
             */
            fun fromString(input: String): Segment? =
                when (input.toLowerCase()) {
                    "a" -> A
                    "b" -> B
                    "c" -> C
                    "d" -> D
                    "e" -> E
                    "f" -> F
                    "g" -> G
                    else -> null
                }
        }
    }

    companion object {
        val ZERO: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.B, Segment.C, Segment.E, Segment.F, Segment.G))
        val ONE: SegmentSignal = SegmentSignal(setOf(Segment.C, Segment.F))
        val TWO: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.C, Segment.D, Segment.E, Segment.G))
        val THREE: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.C, Segment.D, Segment.F, Segment.G))
        val FOUR: SegmentSignal = SegmentSignal(setOf(Segment.B, Segment.C, Segment.D, Segment.F))
        val FIVE: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.B, Segment.D, Segment.F, Segment.G))
        val SIX: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.B, Segment.D, Segment.E, Segment.F, Segment.G))
        val SEVEN: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.C, Segment.F))
        val EIGHT: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.B, Segment.C, Segment.D, Segment.E, Segment.F, Segment.G))
        val NINE: SegmentSignal = SegmentSignal(setOf(Segment.A, Segment.B, Segment.C, Segment.D, Segment.F, Segment.G))

        /**
         * Represents a [SegmentSignal] that is fully on, ie. all of the segments are turned on.
         */
        val ALL: SegmentSignal = EIGHT

        /**
         * Takes a string as input and returns the corresponding SegmentSignal,
         * or null if it cannot be converted to a [SegmentSignal].
         */
        fun fromString(input: String): SegmentSignal? =
            SegmentSignal(input.toLowerCase().map { Segment.fromString(it.toString()) ?: return null }.toSet())
    }
}

/**
 * Represents a mapping from incorrect (ie. jumbled) [SegmentSignal.Segment]s to their correctly decoded
 * [SegmentSignal.Segment] counterpart.
 */
typealias DecodingMapping = Map<SegmentSignal.Segment, SegmentSignal.Segment>

/**
 * Returns true if this [DecodingMapping] represents a complete decoding between all possible
 * [SegmentSignal.Segment]s, false otherwise.
 */
fun DecodingMapping.isComplete(): Boolean {
    return this.containsKey(SegmentSignal.Segment.A) &&
            this.containsKey(SegmentSignal.Segment.B) &&
            this.containsKey(SegmentSignal.Segment.C) &&
            this.containsKey(SegmentSignal.Segment.D) &&
            this.containsKey(SegmentSignal.Segment.E) &&
            this.containsKey(SegmentSignal.Segment.F) &&
            this.containsKey(SegmentSignal.Segment.G)
}

/**
 * Represents a seven-segment signal puzzle, with [uniquePatterns] of non-decoded seven-segment signal patterns
 * and an list of [output] seven-segment signals to decode.
 */
data class SegmentSignalPuzzle(val uniquePatterns: List<SegmentSignal>, val output: List<SegmentSignal>)