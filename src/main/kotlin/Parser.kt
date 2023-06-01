interface Parser<T, C> {
    fun parse(token: String): T

    fun getCompletions(typed: String, context: C): List<String> = ArrayList()
}