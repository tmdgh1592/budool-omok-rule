package rule.other

internal interface Iterator<T> {
    fun hasNext(): Boolean
    fun next(): T
}
