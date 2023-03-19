package rule.wrapper.direction

import rule.*
import rule.other.Iterator

class Directions {
    private val directions = listOf(
        TOP_DIRECTION, BOTTOM_DIRECTION,
        LEFT_DIRECTION, RIGHT_DIRECTION,
        LEFT_BOTTOM_DIRECTION, RIGHT_TOP_DIRECTION,
        LEFT_TOP_DIRECTION, RIGHT_BOTTOM_DIRECTION,
    )

    fun iterator(): Iterator<Direction<Row, Col>> = DirectionsIterator(directions)

    companion object {
        private val TOP_DIRECTION = Direction(1, 0)
        private val BOTTOM_DIRECTION = Direction(-1, 0)
        private val LEFT_DIRECTION = Direction(0, -1)
        private val RIGHT_DIRECTION = Direction(0, 1)
        private val LEFT_BOTTOM_DIRECTION = Direction(-1, -1)
        private val RIGHT_TOP_DIRECTION = Direction(1, 1)
        private val LEFT_TOP_DIRECTION = Direction(1, -1)
        private val RIGHT_BOTTOM_DIRECTION = Direction(-1, 1)
    }
}