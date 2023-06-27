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

    // regex matches --flag | --arg=n | --arg=`n` | -asdf | -asdf=n | -asdf=`n`
    private val parseRegex = Regex("(--([\\w-]+)|-(\\w+))(=(`.+?`|[^ ]+)?)? *")

    fun getCommandCompletions(command: String, context: C, cursorLocation: Int): List<String>? {
        val matches = parseRegex.matchAll(command)
        val lastMatch = matches.firstOrNull { it.range.contains(cursorLocation) } ?: matches.lastOrNull() ?: return null

        val argRange = lastMatch.groups[5]?.range

        val isInArgName = argRange?.contains(cursorLocation)?.not() ?: false


        val (_, _, long, short, arg, _) = lastMatch.groupValues

        return when {
            isInArgName && long.isNotEmpty() -> LongCompletions.getCompletions(long, this)
            isInArgName && short.isNotEmpty() -> ShortCompletions.getCompletions(short, this)

            arg.isEmpty() || isInArgName -> null

            else -> arguments.run {
                getName(long) ?: short.lastOrNull()?.let { getName(it) }
            }?.getCompletions(arg.drop(1).trim('`'), context)
        }
    }

    fun parseCommand(command: String): Map<Flag, *> {
        val matched = parseRegex.matchAll(command)

        val matchedFull = doesLastGoOver(command.length, matched)

        if (!matchedFull) throw TokenizationException("Couldn't match input: last parse was ${matched.lastOrNull()}")

        val outputMap = mutableMapOf<Flag, Any?>()

        print(matched.map { "${it.groupValues}</>" }.forEach(::print))
        println()

        for (match in matched) {
            val (_, _, long, short, arg, _) = match.groupValues

            if (long.isNotEmpty()) {
                val parse: Any?
                val flag: Flag

                if (arg.isNotEmpty()) {
                    parse = arguments.getName(long)?.parse(arg.drop(1).trim('`'))
                        ?: throw ParseException("Couldn't find long argument $long")
                    flag = arguments.getFlag(long) ?: throw ParseException("Couldn't find long argument $long")
                } else {
                    parse = null
                    flag = flags.getFlag(long) ?: throw ParseException("Couldn't find long flag $long")
                }

                outputMap[flag] = parse
            } else if (short.isNotEmpty()) {
                short.dropLast(if (arg.isEmpty()) 0 else 1).forEach {
                    val flag = flags.getFlag(it) ?: throw ParseException("Couldn't find short flag $it")
                    outputMap[flag] = null
                }

                if (arg.isNotEmpty()) {
                    val parse = arguments.getName(short.last())?.parse(arg.drop(1).trim('`'))
                        ?: throw ParseException("Couldn't find short argument ${short.last()}")

                    val flag = arguments.getFlag(short.last())
                        ?: throw ParseException("Couldn't find short argument ${short.last()}")

                    outputMap[flag] = parse
                }
            }
        }

        return outputMap
    }

    object ShortCompletions : Completable<Command<*>> {
        override fun getCompletions(typed: String, context: Command<*>): List<String> {
            val flags = context.flags.flags.mapNotNull { it.shortName }.filterNot { it in typed }.map { "$it" }

            val args = context.arguments.flags.mapNotNull { it.shortName }.filterNot { it in typed }.map { "$it=" }

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
