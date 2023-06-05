class CommandParser<C> {
    private val arguments: FlagMap<Argument<*, C>> = FlagMap()
    private val flags: FlagMap<Boolean> = FlagMap()

    fun addArgument(flag: Flag, argument: Argument<*, C>) {
        arguments.addPair(flag, argument)
    }

    fun addFlag(flag: Flag) {
        flags.addPair(flag, false)
    }

    private val superRegex = Regex("(--([\\w-]+)|-(\\w+))(=(`.+`|[^ ]+))? *")

    fun parsePartial(command: String, context: C, cursorLocation: Int): List<String>? {
        var currentIndex = 0
        var lastIndex = 0
        var lastMatch: MatchResult? = null

        while (currentIndex < cursorLocation) {
            lastIndex = currentIndex

            lastMatch = superRegex.matchAt(command, currentIndex) ?: return null

            currentIndex = lastMatch.range.last + 1
        }

        if (lastMatch == null) return null

        val (_, argName, long, short, eArg, arg) = lastMatch.groupValues
        val cursorOffset = cursorLocation - lastIndex

        val isInArgName = cursorOffset <= argName.length

        return when {
            isInArgName && long.isNotEmpty() -> {
                LongCompletions.getCompletions(long, this)
            }

            isInArgName && short.isNotEmpty() -> ShortCompletions.getCompletions(short, this)

            !isInArgName && eArg.isNotEmpty() -> arguments.run {
                longNames[long] ?: shortNames[short.lastOrNull()]
            }?.second?.parser?.getCompletions(arg.trim('`'), context)

            else -> null
        }
    }

    private fun parseMatch(values: List<String>): Any? {
        val (_, _, long, short, _, arg) = values

        if (short.isNotEmpty()) {
            val flagNames = short.dropLast(1).toCharArray()
            val argName = short.last()

            flagNames.forEach {
                if (flags.shortNames.contains(it)) flags.setShortName(
                    it, true
                ) else throw ParseException("Could not find flag $it")
            }

            return (arguments.shortNames[argName]
                ?: throw ParseException("Could not find short arg $argName")).second.parser.parse(arg)
        }

        if (long.isNotEmpty()) {
            return (arguments.longNames[long]
                ?: throw ParseException("Could not find long arg $long")).second.parser.parse(arg)
        }

        return null
    }

    fun parseCommand(command: String): List<Any?> {
        val matches = mutableListOf<MatchResult>()

        var currentIndex = 0

        while (currentIndex < command.length) {
            matches.add(
                superRegex.matchAt(command, currentIndex)
                    ?: throw TokenizationException("Could not tokenize the input.")
            )
            currentIndex = matches.last().range.last + 1
        }

        return matches.map { parseMatch(it.groupValues) }
    }

    object ShortCompletions : Completable<CommandParser<*>> {
        override fun getCompletions(typed: String, context: CommandParser<*>): List<String> {
            val flags = context.flags.shortNames.keys.map { Pair(it, "") }
            val args = context.arguments.shortNames.keys.map { Pair(it, "=") }

            return flags.plus(args).filterNot { it.first in typed }.map { it.first + it.second }
        }
    }

    object LongCompletions : Completable<CommandParser<*>> {
        override fun getCompletions(typed: String, context: CommandParser<*>): List<String> {
            val flags = context.flags.longNames.keys.map { Pair(it, "") }
            val args = context.arguments.longNames.keys.map { Pair(it, "=") }

            return flags.plus(args).filter { it.first.startsWith(typed) }.map { it.first + it.second }
        }
    }
}