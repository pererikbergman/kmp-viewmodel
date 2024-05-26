class Greeting(
    private val platform: Platform
) : Greeter {
    override fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}

interface Greeter {
    fun greet(): String
}