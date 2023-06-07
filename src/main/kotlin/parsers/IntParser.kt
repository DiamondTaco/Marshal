package parsers

import ParseException
import java.lang.NumberFormatException

class IntParser(private val min: Int = Int.MIN_VALUE, private val max: Int = Int.MAX_VALUE) : Parser<Int, Unit> {
    override fun getCompletions(typed: String, context: Unit): List<String> =
        emptyList() // int has no completions :P

    override fun parse(token: String): Int {
        val parsed = try {
            token.toInt()
        } catch (e: NumberFormatException) {
            throw ParseException(e.localizedMessage)
        }

        if (parsed < min) throw ParseException("Int should be no less than $min")
        if (parsed > max) throw ParseException("Int should be no greater than $max")

        return parsed
    }
}