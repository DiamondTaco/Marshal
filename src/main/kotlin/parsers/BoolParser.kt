package parsers

import ParseException

class BoolParser : Parser<Boolean, Unit> {
    override fun getCompletions(typed: String, context: Unit): List<String> {
        return listOf("true", "false").filter { it.startsWith(typed, true) }
    }

    override fun parse(token: String): Boolean {
        return try {
            token.lowercase().toBooleanStrict()
        } catch (e: IllegalArgumentException) {
            throw ParseException(e.localizedMessage)
        }
    }
}