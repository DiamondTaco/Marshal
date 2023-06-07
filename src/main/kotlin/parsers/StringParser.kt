package parsers

class StringParser : Parser<String, Unit> {
    override fun getCompletions(typed: String, context: Unit): List<String> =
        emptyList() // don't know any completions

    override fun parse(token: String): String = token
}