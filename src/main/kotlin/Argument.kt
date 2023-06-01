class Argument<T, C> {
    lateinit var longName: String
    lateinit var parser: Parser<T, C>
    var shortName: String? = null
    var defaultSupplier: (() -> T)? = null

    fun withName(longName: String) = apply { this.longName = longName }

    fun withShort(shortName: Char) = apply { this.shortName = shortName.toString() }

    fun withParser(parser: Parser<T, C>) = apply { this.parser = parser }

    fun withDefault(default: T) = apply { this.defaultSupplier = { default } }

    fun withDefaultSupplier(defaultSupplier: () -> T) = apply { this.defaultSupplier = defaultSupplier }
}

