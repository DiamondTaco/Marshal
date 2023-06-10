import parsers.BoolParser
import parsers.DefaultParser
import parsers.IntParser

operator fun <T> List<T>.component6() = this[5]


fun main() {
    testStuff()
}

fun testStuff() {
    val parser = Command<Unit>().apply {
        addArgument(Flag("hello-world"), DefaultParser(BoolParser()) { false })
        addArgument(Flag("lmao-int", 'l'), IntParser(-30, 30))
        addFlag(Flag("do-stuff", 'd'))
        addFlag(Flag("do-more-stuff", 'm'))
    }

    val commandPartial = "--hello-world=true -ml=5 -d"
    //                               ^11  ^16    ^23

    println(parser.parsePartial(commandPartial, Unit, 11))
    println(parser.parsePartial(commandPartial, Unit, 16))
    println(parser.parsePartial(commandPartial, Unit, 23))
}

fun Regex.matchAll(input: CharSequence): List<MatchResult>? {
    val matches = generateSequence(matchAt(input, 0)) { it.next() }.toList()

    if (matches.last().run { range.last <= input.length }) return null

    return matches
}