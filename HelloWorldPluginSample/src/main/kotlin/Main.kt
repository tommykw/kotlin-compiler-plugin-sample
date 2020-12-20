fun main() {
    foo()
}

@HelloWorld
fun foo(): String {
    return "foo"
}

annotation class HelloWorld
