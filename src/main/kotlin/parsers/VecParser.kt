package parsers

import ParseException
import matchAll

class VecParser<T, C>(private val parser: Parser<T, C>, private val length: Int? = null) : Parser<List<T>, C> {

    private val parseRegex = Regex("(`.+?`|[^,]+),? *")

    override fun getCompletions(typed: String, context: C): List<String> {
        if (typed.isEmpty()) return emptyList()

        val splitTyped = typed.split(',')

        if (length != null && splitTyped.size > length) return emptyList()

        return parser.getCompletions(splitTyped.last(), context)
            .map { it + if (splitTyped.size == length) "" else ", " }
    }

    override fun parse(token: String): List<T> {
        val splitToken = parseRegex.matchAll(token) ?: throw ParseException("Could not split input into list")

        if (length != null && splitToken.size != length) {
            throw ParseException("List should be of length $length, was ${splitToken.size}")
        }

        return splitToken
            .map { it.groupValues[2] }
            .map { it.trim('`') }
            .map { parser.parse(it) }
    }
}