package problems.problem3

/**
 * Given a list of binary numbers, calculates the product of the gamma and epsilon properties.
 * All [binaryNumbers] must be the same number of binary numbers long - if not, returns null.
 */
fun calculateBinaryProduct(binaryNumbers: List<Binary>): Int? {
    // Enforce our precondition
    val numDigits = binaryNumbers.first().digits.size
    if (binaryNumbers.any { it.digits.size != numDigits }) return null

    // gamma is the Binary number composed of the most frequent digits
    val gamma = binaryNumbers.fold((Pair(Array(numDigits) { 0 }, Binary.zero(numDigits)))) { (frequency, mostFrequent), cur ->
        // for every position:
        //   if the current digit is the most frequent, increment the frequency
        //   otherwise if the current digit is not the most frequent, decrement the frequency
        //   if the frequency is now negative, negate it and swap the most frequent
        for (i in 0 until numDigits) {
            if (cur.digits[i] == mostFrequent.digits[i]){
                frequency[i]++
            } else {
                frequency[i]--
            }
            if (frequency[i] < 0) {
                frequency[i] *= -1
                if (mostFrequent.digits[i] == Binary.Digit.Zero) {
                    mostFrequent.digits[i] = Binary.Digit.One
                } else {
                    mostFrequent.digits[i] = Binary.Digit.Zero
                }
            }
        }
        Pair(frequency, mostFrequent)
    }.second

    // epsilon is the Binary number composed of the most frequent digits, which is equivalent
    // to flipping every bit in gamma
    val epsilon = Binary(gamma.digits.copyOf().map { if (it is Binary.Digit.One) Binary.Digit.Zero else Binary.Digit.One }.toTypedArray())

    return gamma.toInt() * epsilon.toInt()
}

/**
 * Calculate the Life Support rating, which is the Binary Product of two numbers derived from the input of
 * [binaryNumbers].
 * All [binaryNumbers] must be the same number of binary numbers long - if not, returns null.
 */
fun calculateBinaryLifeSupportRating(binaryNumbers: List<Binary>): Int? {
    // Enforce our precondition
    val numDigits = binaryNumbers.first().digits.size
    if (binaryNumbers.any { it.digits.size != numDigits }) return null

    // oxygenRating is the Binary last binary number left after removing all the numbers with least frequent digits
    val numbersForOxygen = binaryNumbers.toMutableList()
    var oxygenDigitIndex = 0
    while (numbersForOxygen.size > 1) {
        // find the least frequent
        var countOfOnes = 0
        for (i in 0 until numbersForOxygen.size) {
            if (numbersForOxygen[i].digits[oxygenDigitIndex] == Binary.Digit.One) countOfOnes++
        }
        val countOfZeroes = numbersForOxygen.size - countOfOnes
        val leastFrequent = if (countOfOnes < countOfZeroes) Binary.Digit.One else Binary.Digit.Zero

        // remove all the numbers with the least frequent digit
        var i = 0
        while (i < numbersForOxygen.size) {
            if (numbersForOxygen[i].digits[oxygenDigitIndex] == leastFrequent) {
                numbersForOxygen.removeAt(i)
                i-- // move the index back by one to account for the fact we just removed an number
            }
            i++
        }

        oxygenDigitIndex++
    }
    val oxygenRating = numbersForOxygen.first()

    // co2Rating is the Binary last binary number left after removing all the numbers with least frequent digits
    val numbersForCo2 = binaryNumbers.toMutableList()
    var co2DigitIndex = 0
    while (numbersForCo2.size > 1) {
        // find the most frequent
        var countOfOnes = 0
        for (i in 0 until numbersForCo2.size) {
            if (numbersForCo2[i].digits[co2DigitIndex] == Binary.Digit.One) countOfOnes++
        }
        val countOfZeroes = numbersForCo2.size - countOfOnes
        val mostFrequent = if (countOfOnes >= countOfZeroes) Binary.Digit.One else Binary.Digit.Zero

        // remove all the numbers with the most frequent digit
        var i = 0
        while (i < numbersForCo2.size) {
            if (numbersForCo2[i].digits[co2DigitIndex] == mostFrequent) {
                numbersForCo2.removeAt(i)
                i-- // move the index back by one to account for the fact we just removed an number
            }
            i++
        }

        co2DigitIndex++
    }
    val co2Rating = numbersForCo2.first()

    return oxygenRating.toInt() * co2Rating.toInt()
}

/**
 * Represents a binary number, which is composed of a series of ordered binary digits.
 */
class Binary(val digits: Array<Digit>) {
    sealed class Digit {
        abstract fun asString(): String

        object Zero: Digit() {
            override fun asString() = "0"
        }

        object One: Digit() {
            override fun asString() = "1"
        }
    }

    /**
     * Converts this Binary representation into String form.
     */
    fun asString(): String = digits.fold("") { acc, digit -> acc + digit.asString() }

    /**
     * Converts this Binary representation into Int form.
     */
    fun toInt(): Int {
        val (result, _) = digits.foldRight(Pair(0, 1)) { digit, (currentValue, multiplier) ->
            when (digit) {
                Digit.One -> Pair(currentValue + multiplier, multiplier * 2)
                Digit.Zero -> Pair(currentValue, multiplier * 2)
            }
        }
        return result
    }

    companion object {
        /**
         * Creates the Binary for 0 with [numDigits] digits. For example, zero(5) would yield a Binary of '00000'.
         */
        fun zero(numDigits: Int = 1): Binary = Binary(Array(numDigits) { Digit.Zero })

        /**
         * Parses [input] String into a Binary. [input] must only contain the characters '0' and '1'. Otherwise,
         * null is returned.
         */
        fun fromString(input: String): Binary? {
            return Binary(input.map {
                when (it) {
                    '0' -> Digit.Zero
                    '1' -> Digit.One
                    else -> return null
                }
            }.toTypedArray())
        }
    }
}