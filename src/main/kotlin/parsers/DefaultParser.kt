package parsers

import ParseException
import kotlin.properties.Delegates

class DefaultParser<T, C>(val parser: Parser<T, C>, val defaultSupplier: () -> T) : Parser<T, C> {
    override fun getCompletions(typed: String, context: C): List<String> =
        parser.getCompletions(typed, context)
    override fun parse(token: String): T =
        try {
            parser.parse(token)
        } catch (e: ParseException) {
            defaultSupplier()
        }
}