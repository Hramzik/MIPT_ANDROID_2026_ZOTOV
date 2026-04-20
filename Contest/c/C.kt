// Submission id 157622075
class BinaryTree<T : Comparable<T>> {

    private var root: Node<T>? = null
    private var size = 0

    private class Node<T : Comparable<T>>(
            val value: T,
            var left: Node<T>? = null,
            var right: Node<T>? = null
    ) {
        fun insertIntoSubTree(newValue: T) {
            if (newValue <= this.value) {
                left = left?.apply { insertIntoSubTree(newValue) } ?: Node(newValue)
            } else {
                right = right?.apply { insertIntoSubTree(newValue) } ?: Node(newValue)
            }
        }
    }

    fun insert(value: T) {
        root = root?.apply { insertIntoSubTree(value) } ?: Node(value)
        ++size
    }

    fun isEmpty(): Boolean = root == null
    fun size(): Int = size

    fun preOrderIterator(): Iterator<T> = PreOrderIterator(root)

    private inner class PreOrderIterator(first: Node<T>?) : Iterator<T> {
        private val stack = ArrayDeque<Node<T>>().apply { first?.let { addLast(it) } }

        override fun hasNext(): Boolean = stack.isNotEmpty()

        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            val node = stack.removeLast()

            node.right?.let { stack.addLast(it) }
            node.left?.let { stack.addLast(it) }

            return node.value
        }
    }
}

fun main() {
    val n = readln().toInt()
    val tree = BinaryTree<Int>()

    repeat(n) {
        val value = readln().toInt()
        tree.insert(value)
    }

    val iterator = tree.preOrderIterator()
    while (iterator.hasNext()) {
        print("${iterator.next()} ")
    }
}
