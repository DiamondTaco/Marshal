interface Parser<T, C> : Completable<C> {
    fun parse(token: String): T
}