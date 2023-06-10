import parsers.BoolParser
import parsers.IntParser

operator fun <T> List<T>.component6() = this[5]


fun main() {
    testStuff()
}

fun testStuff() {
    val parser = Command<Unit>().apply {
        addArgument(Flag("hello-world"), Argument(BoolParser()) { false })
        addArgument(Flag("lmao-int", 'l'), Argument(IntParser(-30, 30)))
        addFlag(Flag("do-stuff", 'd'))
        addFlag(Flag("do-more-stuff", 'm'))
    }

    val commandPartial = "--hello-world=true -ml=5 -d"

    println(parser.parseCommand(commandPartial))
}

fun Regex.matchAll(input: CharSequence): List<MatchResult>? {
    val matches = generateSequence(matchAt(input, 0)) { it.next() }.toList()

    if (matches.lastOrNull()?.run { range.last < input.length - 1} != false) return null

    return matches
}