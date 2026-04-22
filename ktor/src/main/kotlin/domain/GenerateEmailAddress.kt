package domain

import model.WordLists
import kotlin.math.log
import kotlin.math.pow
import kotlin.mod

class GenerateEmailAddress(
    private val databaseRepository: DatabaseRepository,
    private val wordLists: WordLists
) {

    private val largestAcceptedValue: Long by lazy {
        wordLists.adjectives.size * wordLists.nouns.size * 1000L
    }

    private val modulo: Long by lazy {
        val exponent = (log(largestAcceptedValue.toDouble(), 2.0) + 1).toInt()
        2.toDouble().pow(exponent).toLong()
    }

    suspend operator fun invoke(): String {
        
        //Intentional non-optimal solution. It's hard to use the exactly needed modulo for LCG,
        //but much easier to use a power of two, which is too large. This solution results in
        //up to 99,99% avg decreased speed in generating the next value, and a potential race condition
        //causing to skip over a value. Acceptable.
        var value = Long.MAX_VALUE

        while (value > largestAcceptedValue) {
            value = databaseRepository.getNextLcgValue(modulo)
        }

        val fullThousands = value / 1000
        val numberSuffix = value.mod(1000) - 1

        val aIndex = (fullThousands / wordLists.nouns.size).toInt()
        val nIndex = fullThousands.mod(wordLists.nouns.size)
        val tld = System.getenv("POSTFIX_DOMAIN")

        return "${wordLists.adjectives[aIndex]}.${wordLists.nouns[nIndex]}$numberSuffix@$tld"
    }
}