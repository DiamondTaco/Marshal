import parsers.Parser

class Command<C> {
    private val arguments: FlagMap<Parser<*, C>> = FlagMap()
    private val flags: FlagMap<Boolean> = FlagMap()

    fun addArgument(flag: Flag, argument: Parser<*, C>) {
        arguments.addPair(flag, argument)
    }

    fun addFlag(flag: Flag) {
        flags.addPair(flag, false)
    }

    private val parseRegex = Regex("(--([\\w-]+)|-(\\w+))(=(`.+?`|[^ ]+)?)? *")

    fun parsePartial(command: String, context: C, cursorLocation: Int): List<String>? {
        val matches = parseRegex.matchAll(command)

        val lastMatch = matches?.firstOrNull { it.range.contains(cursorLocation) } ?: return null

        val (_, _, long, short, arg, _) = lastMatch.groupValues

        val isInArgName = cursorLocation < lastMatch.range.last - arg.length

        return when {
            isInArgName && long.isNotEmpty() -> {
                LongCompletions.getCompletions(long, this)
            }

            isInArgName && short.isNotEmpty() -> ShortCompletions.getCompletions(short, this)

            !isInArgName && arg.isNotEmpty() -> arguments.run {
                getName(long) ?: short.lastOrNull()?.let { getName(it) }
            }?.getCompletions(arg.drop(1).trim('`'), context)

            else -> null
        }
    }

//    fun parseCommand(command: String): Map<Flag, *> {
//        val matched = parseRegex.matchAll(command).takeIf { it }
//    }

    object ShortCompletions : Completable<Command<*>> {
        override fun getCompletions(typed: String, context: Command<*>): List<String> {
            val flags = context.flags.flags
                .mapNotNull { it.shortName }
                .filterNot { it in typed }
                .map { "$it" }

            val args = context.arguments.flags
                .mapNotNull { it.shortName }
                .filterNot { it in typed }
                .map { "$it=" }

            return flags.plus(args)
        }
    }

    object LongCompletions : Completable<Command<*>> {
        override fun getCompletions(typed: String, context: Command<*>): List<String> {
            val flags = context.flags.flags.map { it.longName }
            val args = context.arguments.flags.map { "${it.longName}=" }

            return flags.plus(args).filter { it.startsWith(typed) }
        }
    }
}