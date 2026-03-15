// Submission id 157622194
class Stack<T : Comparable<T>> {

    private var top: Node<T>? = null
    private var size = 0

    private class Node<T : Comparable<T>>(
            val value: T,
            val maxHereAndBelow: T,
            var prev: Node<T>? = null
    )

    fun push(value: T) {
        val newMax = maxOf(value, top?.maxHereAndBelow ?: value)
        val newNode = Node(value, newMax)
        newNode.prev = top
        top = newNode
        ++size
    }

    fun pop(): T? {
        val popped = top?.value

        top?.let {
            top = it.prev
            size--
        }

        return popped
    }

    fun max(): T {
        return top?.maxHereAndBelow ?: throw NoSuchElementException()
    }

    fun isEmpty(): Boolean = top == null
}

fun main() {
    val q = readln().toInt()
    val stack = Stack<Int>()

    repeat(q) {
        val line = readln().split(" ")
        when (line[0]) {
            "push" -> stack.push(line[1].toInt())
            "pop" -> stack.pop()
            "max" -> println(stack.max())
        }
    }
}
