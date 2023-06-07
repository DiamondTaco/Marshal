import parsers.Parser

data class Argument<T, C>(val parser: Parser<T, C>, val defaultSupplier: (() -> T)? = null)
