operator fun <T> List<T>.component6() = this[5]


fun main() {
    val parser = CommandParser<Unit>()

    val emptyArg = Argument(object : Parser<Nothing, Unit> {
        override fun getCompletions(typed: String, context: Unit): List<String> {
            TODO("Not yet implemented")
        }

        override fun parse(token: String): Nothing {
            TODO("Not yet implemented")
        }

    })

    parser.addArgument(Flag("fjeesder", 'f'), emptyArg)
    parser.addArgument(Flag("djesset", 'j'), emptyArg)
    parser.addArgument(Flag("ssle", 'e'), emptyArg)
    parser.addFlag(Flag("a_", 'a'))
    parser.addFlag(Flag("b_", 'b'))
    parser.addFlag(Flag("c_", 'c'))

    val command = "--aot=3d -aet  --fjee=`{sdfkj sdkfj sldfj sjdj`"

    val x = parser.parsePartial(command, Unit, 11)
    val y = parser.parsePartial(command, Unit, 19)


    println("$x @ ${command[11]}, $y @ ${command[17]}")
}