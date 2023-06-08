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
        val matches = superRegex.matchAll(command)

        val lastMatch = matches?.firstOrNull { it.range.last >= cursorLocation } ?: return null
        val lastIndex = lastMatch.range.first

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

    private fun parseMatch(values: List<String>): Pair<Flag, *>? {
        val (_, _, long, short, _, arg) = values

        if (short.isNotEmpty()) {
            val flagNames = (if (arg.isEmpty()) short else short.dropLast(1)).toCharArray()

            flagNames.forEach {
                if (flags.shortNames.contains(it)) flags.setShortName(
                    it, true
                ) else throw TokenizationException("Could not find short flag $it")
            }

            if (arg.isEmpty()) return null

            val shortArg = arguments.shortNames[short.last()] //
                ?: throw TokenizationException("Could not find short arg ${short.last()}")
            return Pair(Flag(shortArg.first, short.last()), shortArg.second.parser.parse(arg))
        }

        if (long.isNotEmpty()) {
            return if (arg.isEmpty()) {
                if (flags.longNames.contains(long)) flags.setLongName(
                    long, true
                ) else throw TokenizationException("Could not find long flag $long")
                null
            } else {
                val longArg = arguments.longNames[long] ?: throw TokenizationException("Could not find long arg $long")
                Pair(Flag(long, longArg.first), longArg.second.parser.parse(arg))
            }
        }

        throw TokenizationException("Empty argument")
    }

    @Throws(TokenizationException::class, ParseException::class)
    fun parseCommand(command: String): Map<Flag, *> {
        val matches = superRegex.matchAll(command) ?: throw TokenizationException("Could not tokenize the input.")

        return matches.mapNotNull { parseMatch(it.groupValues) }.toMap()
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