import parsers.IntParser
import parsers.Parser
import com.mojang.brigadier.CommandDispatcher

operator fun <T> List<T>.component6() = this[5]


fun main() {
    val dispatcher = CommandDispatcher<Int>()


    testStuff()
}

fun testStuff() {

    val parser = CommandParser<Unit>()




    val emptyArg = Argument(object : Parser<Nothing, Unit> {
        override fun getCompletions(typed: String, context: Unit): List<String> =
            listOf("Balls")

        override fun parse(token: String): Nothing {
            TODO("Not yet implemented")
        }

    })



    parser.addArgument(Flag("fjeesder", 'f'), Argument(IntParser()))
    parser.addArgument(Flag("djesset", 'j'), emptyArg)
    parser.addArgument(Flag("ssle", 'e'), emptyArg)
    parser.addFlag(Flag("a_", 'a'))
    parser.addFlag(Flag("b_", 'b'))
    parser.addFlag(Flag("c_", 'c'))

    val command = "--aot=3d -aet  --fjeesder=3352"

    val x = parser.parsePartial(command, Unit, 11)
    val y = parser.parsePartial(command, Unit, 19)
    val z = parser.parsePartial(command, Unit, 29)

    val commandB = "--fjeesder=3352"
    val parsed = parser.parseCommand(commandB)

    println(parsed)


    println("$x @ ${command[11]}, $y @ ${command[19]}, $z @ ${command[29]}")
}