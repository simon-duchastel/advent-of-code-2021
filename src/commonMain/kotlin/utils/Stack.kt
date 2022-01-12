package utils

/**
 * Represents a FIFO (First-In First-Out) Stack.
 */
interface Stack<E> {
    /**
     * Take the last element of the Stack and return that element, removing it from the Stack. Returns null
     * if the Stack is empty.
     */
    fun pop(): E?

    /**
     * Put [element] on the top of the Stack.
     */
    fun push(element: E)

    /**
     * Take the last element of the Stack and return that element, keeping it on the Stack. Returns null
     * if the Stack is empty.
     */
    fun peek(): E?

    /**
     * Clear the entire Stack of all values.
     */
    fun clear()

    /**
     * Reverse the Stack, flipping each element's position from front-to-end.
     */
    fun reverse()
}

/**
 * Mutable implementation of [Stack].
 */
class MutableStack<E>(initialElements: List<E> = emptyList()): Stack<E> {
    private val elements = initialElements.toMutableList()

    override fun pop(): E? {
        if (elements.isEmpty()) return null
        return elements.removeAt(0)
    }

    override fun push(element: E) {
        elements.add(0, element)
    }

    override fun peek(): E? {
        return elements.getOrNull(0)
    }

    override fun clear() {
        elements.clear()
    }

    override fun reverse() {
        elements.reverse()
    }
}