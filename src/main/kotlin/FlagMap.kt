class FlagMap<T> {
    private val longNames: MutableMap<String, Pair<Char?, T>> = mutableMapOf()
    private val shortNames: MutableMap<Char, Pair<String, T>> = mutableMapOf()

    fun addPair(key: Flag, value: T) {
        longNames[key.longName] = Pair(key.shortName, value)
        key.shortName?.let { shortNames[it] = Pair(key.longName, value) }
    }

    fun setName(longName: String, value: T) {
        val shortName = longNames[longName]?.first
        longNames[longName] = Pair(shortName, value)
        shortName?.let { shortNames[it] = Pair(longName, value) }
    }

    fun setName(shortName: Char, value: T) {
        val longName = shortNames[shortName]?.first ?: return

        shortNames[shortName] = Pair(longName, value)
        longNames[longName] = Pair(shortName, value)
    }

    fun getName(longName: String): T? =
        longNames[longName]?.second

    fun getName(shortName: Char): T? =
        shortNames[shortName]?.second

    val flags get() = longNames.map { Flag(it.key, it.value.first) }
}