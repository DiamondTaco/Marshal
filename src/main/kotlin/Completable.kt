interface Completable<C> {
    fun getCompletions(typed: String, context: C): List<String>
}