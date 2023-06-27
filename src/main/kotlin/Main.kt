import parsers.*

operator fun <T> List<T>.component6() = this[5]


fun main() {
    val parser = IntParser(-30, 30)
    println(parser.parse("asdf"))
    testStuff()
}


enum class Direction {
    North, South, East, West, Northeast, Northwest, Southeast, Southwest,
}

fun testStuff() {
    val parser = Command<Unit>().apply {
        addArgument(Flag("hello-world"), DefaultParser(BoolParser()) { false })
        addArgument(Flag("hello-b", 'l'), IntParser(-30, 30))
        addArgument(Flag("hello-c"), EnumParser(Direction.entries))
        addArgument(Flag("l-plus-ratio"), DefaultParser(IntParser(-30, 30)) { 31 })
        addFlag(Flag("do-stuff", 'd'))
        addFlag(Flag("do-more-stuff", 'm'))
    }

    val commandPartial = "--l-plus-ratio=asdf --hello-c=North"

    println(commandPartial[29])
    println(parser.getCommandCompletions(commandPartial, Unit, 30))
    println(parser.parseCommand(commandPartial))
}

fun Regex.matchAll(input: CharSequence): Sequence<MatchResult> = generateSequence(matchAt(input, 0)) { it.next() }

fun doesLastGoOver(length: Int, inputSequence: Sequence<MatchResult>): Boolean =
    inputSequence.lastOrNull()?.takeUnless { it.range.last < length - 1 } != null