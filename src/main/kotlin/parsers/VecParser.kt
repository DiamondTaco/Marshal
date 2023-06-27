package parsers

import ParseException
import matchAll

class VecParser<T, C>(private val parser: Parser<T, C>, private val length: Int? = null) : Parser<List<T>, C> {

    // matches `a`,| `a`,   | as df abc, | `a,a`
    private val listRegex = Regex("(`.+?`|[^,]+),? *")

    override fun getCompletions(typed: String, context: C): List<String> {
        if (typed.isEmpty()) return emptyList()

        val splitTyped = listRegex.matchAll(typed).toList()

        if (length != null && splitTyped.size > length) return emptyList()

        val lastTyped = splitTyped.lastOrNull()?.groupValues?.get(1) ?: ""

        return parser.getCompletions(lastTyped, context)
            .map { it + if (splitTyped.size == length) "" else ", " }
    }

    override fun parse(token: String): List<T> {
        val splitToken = listRegex.matchAll(token).toList()

        if (length != null && splitToken.size != length) {
            throw ParseException("List should be of length $length, was ${splitToken.size}")
        }

        return splitToken
            .map { it.groupValues[2] }
            .map { it.trim('`') }
            .map { parser.parse(it) }
    }
}