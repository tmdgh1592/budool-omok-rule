package rule

typealias Row = Int
typealias Col = Int
typealias MoveWeight = Int

typealias Position<R, C> = Pair<R, C>
typealias Direction<R, C> = Pair<R, C>

abstract class OmokRule(
    private val boardWidth: Row,
    private val boardHeight: Col,
) {
    /**
     * The function will return True if any of the three forbidden moves '3-3', '4-4', and 'overline' is detected.
     * @param blackPositions List of pairs for row and column of black stones.
     * @param whitePositions List of pairs for row and column of white stones.
     * @param startPosition The row and column of the stone that is being placed.
     * @return The result of checking all numbers.
     * */
    fun checkAll(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule = if (listOf(
            checkThreeToThreePoint(blackPositions, whitePositions, startPosition),
            checkFourToFourPoint(blackPositions, whitePositions, startPosition),
            checkOverline(blackPositions, startPosition)
        ).any { it.state }
    ) KoRule.KO_ALL else KoRule.NOT_KO

    /**
     * check 'three-three' point
     * @param blackPositions List of pairs for row and column of black stones.
     * @param whitePositions List of pairs for row and column of white stones.
     * @param startPosition The row and column of the stone that is being placed.
     * @return Whether the given row and column correspond to 3-3.
     * */
    abstract fun checkThreeToThreePoint(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule

    /**
     * Check 'four-four' point.
     * @param blackPositions List of pairs for row and column of black stones.
     * @param whitePositions List of pairs for row and column of white stones.
     * @param startPosition The row and column of the stone that is being placed.
     * @return Returns whether the given row and column correspond to 4-4.
     * */
    abstract fun checkFourToFourPoint(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule

    /**
     * Check 'overline' pattern.
     * @param stonesPositions List of stone positions for the given row and column to check for overline.
     * @param startPosition The row and column of the stone that is being placed.
     * @return Boolean value indicating whether it is overline.
     * */
    abstract fun checkOverline(
        stonesPositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule

    protected fun inRange(row: Int, col: Int) = (row in 1..boardHeight) && (col in 1..boardWidth)

    protected fun List<Position<Row, Col>>.isPlaced(row: Int, col: Int): Boolean = contains(Pair(row, col))
}
