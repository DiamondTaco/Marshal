class FlagMap<T> {
    val longNames: MutableMap<String, Pair<Char?, T>> = mutableMapOf()
    val shortNames: MutableMap<Char, Pair<String, T>> = mutableMapOf()

    fun addPair(key: Flag, value: T) {
        longNames[key.longName] = Pair(key.shortName, value)
        key.shortName?.let { shortNames[it] = Pair(key.longName, value) }
    }

    fun setLongName(longName: String, value: T) {
        val shortName = longNames[longName]?.first
        longNames[longName] = Pair(shortName, value)
        shortName?.let { shortNames[it] = Pair(longName, value) }
    }

    fun setShortName(shortName: Char, value: T) {
        val longName = shortNames[shortName]?.first ?: return

        shortNames[shortName] = Pair(longName, value)
        longNames[longName] = Pair(shortName, value)
    }
}