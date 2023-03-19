package rule

interface Iterator<T> {
    fun hasNext(): Boolean
    fun next(): T
}