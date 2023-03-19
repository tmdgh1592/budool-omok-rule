package rule.wrapper.direction

import rule.Col
import rule.Direction
import rule.other.Iterator
import rule.Row

internal class DirectionsIterator(items: List<Direction<Row, Col>>) : Iterator<Direction<Row, Col>> {
    private val _items: MutableList<Direction<Row, Col>> = items.deepCopy()

    override fun hasNext(): Boolean = _items.isNotEmpty()

    override fun next(): Direction<Row, Col> {
        if (hasNext()) {
            return _items.removeFirst()
        }
        throw IllegalStateException("The next direction does not exist.")
    }

    private fun List<Direction<Row, Col>>.deepCopy(): MutableList<Direction<Row, Col>> =
        map { it.copy() }.toMutableList()
}