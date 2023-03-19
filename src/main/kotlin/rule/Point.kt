package rule

data class Point(val row: Row, val col: Col) {
    fun move(rowStep: Int, colStep: Int): Point = Point(row + rowStep, col + colStep)

    fun inRange(rowBound: Row, colBound: Col): Boolean =
        (row in 1..rowBound) && (col in 1..colBound)
}
