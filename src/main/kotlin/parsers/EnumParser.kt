package parsers

import kotlin.enums.EnumEntries

class EnumParser<T : Enum<T>>(private val entries: EnumEntries<T>) : Parser<T, Unit> {
    override fun getCompletions(typed: String, context: Unit): List<String> =
        entries.map { it.name }.filter { it.startsWith(typed, true) }


    override fun parse(token: String): T = entries.first { it.name == token }
}