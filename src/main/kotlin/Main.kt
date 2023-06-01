fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    val intArgument = Argument<Int, Nothing?>()
        .withName("huge_balls")
        .withShort('h')
        .withParser(object : Parser<Int, Nothing?> {
            override fun parse(token: String) = 35
        })
        .withDefault(22)
        .withDefaultSupplier { 28 }

    println(intArgument)
}