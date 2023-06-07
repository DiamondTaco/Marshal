package parsers

import ParseException
import java.lang.NumberFormatException

class DoubleParser(
    private val min: Double = Double.NEGATIVE_INFINITY,
    private val max: Double = Double.POSITIVE_INFINITY,
) : Parser<Double, Unit> {
    override fun getCompletions(typed: String, context: Unit): List<String> =
        emptyList() // also no float completions

    override fun parse(token: String): Double {
        val parsed = try {
            token.toDouble()
        } catch (e: NumberFormatException) {
            throw ParseException(e.localizedMessage)
        }

        if (parsed < min) throw ParseException("Double should be no less than $min")
        if (parsed > max) throw ParseException("Double should be no greater than $max")

        return parsed
    }
}