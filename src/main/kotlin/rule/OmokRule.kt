package rule

import rule.type.Foul
import rule.type.KoRule
import rule.wrapper.direction.Directions
import rule.wrapper.point.Point

typealias Row = Int
typealias Col = Int
typealias MoveWeight = Int

typealias Direction<R, C> = Pair<R, C>

abstract class OmokRule(
    protected val boardWidth: Row,
    protected val boardHeight: Col,
) {
    /**
     * The function will determine if the win condition is satisfied.
     * If it is a black stone, it will determine whether there is a foul.
     * On the other hand, whiteStone only checks that the victory condition is satisfied regardless of whether there is a foul play or not.
     *
     * @param blackPoints List of pairs for row and column of black stones.
     * @param whitePoints List of pairs for row and column of white stones.
     * @param startPoint The row and column of the stone that is being placed.
     *
     * @return Returns true if no fouls are played and the win conditions are met.
     * */
    fun checkWin(blackPoints: List<Point>, whitePoints: List<Point>, startPoint: Point): Boolean {
        val satisfyWin = checkSerialSameStonesBiDirection(blackPoints, startPoint, WIN_STANDARD)
        val koState = checkAllFoulCondition(blackPoints, whitePoints, startPoint)

        if (satisfyWin && koState == KoRule.NOT_KO) {
            return true
        }
        return false
    }

    /**
     * The function will return True if any of the three forbidden moves '3-3', '4-4', and 'overline' is detected.
     *
     * @param blackPoints List of pairs for row and column of black stones.
     * @param whitePoints List of pairs for row and column of white stones.
     * @param startPoint The row and column of the stone that is being placed.
     *
     * @return The result of checking all numbers.
     * */
    fun checkAllFoulCondition(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
    ): KoRule = if (listOf(
            checkDoubleFoul(blackPoints, whitePoints, startPoint, Foul.DOUBLE_THREE),
            checkDoubleFoul(blackPoints, whitePoints, startPoint, Foul.DOUBLE_FOUR),
            checkOverline(blackPoints, startPoint)
        ).any { it.state }
    ) KoRule.KO_ALL else KoRule.NOT_KO

    /**
     * check 'three-three' point or 'four-four' point according to the given 'foul type'
     *
     * @param blackPoints List of pairs for row and column of black stones.
     * @param whitePoints List of pairs for row and column of white stones.
     * @param startPoint The row and column of the stone that is being placed.
     *
     * @return Whether the given row and column correspond to 3-3 or 4-4 according to the given 'foul type'.
     * */
    abstract fun checkDoubleFoul(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        foul: Foul,
    ): KoRule

    /**
     * Check 'overline' pattern.
     *
     * @param stonesPoints List of stone points for the given row and column to check for overline.
     * @param startPoint The row and column of the stone that is being placed.
     *
     * @return Boolean value indicating whether it is overline.
     * */
    abstract fun checkOverline(
        stonesPoints: List<Point>,
        startPoint: Point,
    ): KoRule

    /**
     * When a stone is placed at a specific location, it checks if the same number of stones are in a row.
     * It can also be used to determine if you have won.
     *
     * @param stonesPoints List of stone points for the given row and column to check for continuous.
     * @param startPoint The row and column of the stone that is being placed.
     *
     * @return Return if there are as many stones in a row as you are looking for.
     * */
    fun checkSerialSameStonesBiDirection(
        stonesPoints: List<Point>,
        startPoint: Point,
        sameStoneToCheck: Int,
    ): Boolean {
        val dirIterator = Directions().iterator()

        while (dirIterator.hasNext()) {
            val forwardCount = countSerialStonesOneDirection(stonesPoints, startPoint, dirIterator.next())
            val backCount = countSerialStonesOneDirection(stonesPoints, startPoint, dirIterator.next())
            val totalMoveCount = forwardCount + backCount - 1
            if (totalMoveCount >= sameStoneToCheck) return true
        }
        return false
    }

    /**
     * Returns the number of identical stones in a row in one direction.
     *
     * @param stonesPoints List of stone points for the given row and column to check for continuous.
     * @param startPoint The row and column of the stone that is being placed.
     * @param direction Direction you want to explore.
     *
     * @return The number of identical stones placed in a given direction.
     * */
    private fun countSerialStonesOneDirection(
        stonesPoints: List<Point>,
        startPoint: Point,
        direction: Direction<Row, Col>,
    ): Int {
        var sameStoneCount = DEFAULT_SAME_STONE_COUNT
        val rowStep = direction.first
        val colStep = direction.second
        var curPoint = startPoint.move(rowStep, colStep)

        while (curPoint.inRange(boardWidth, boardHeight) && stonesPoints isPlaced curPoint) {
            sameStoneCount++
            curPoint = curPoint.move(rowStep, colStep)
        }
        return sameStoneCount
    }

    protected infix fun List<Point>.isPlaced(point: Point): Boolean = contains(point)

    companion object {
        @JvmStatic
        protected val WIN_STANDARD: Int = 5
        private const val DEFAULT_SAME_STONE_COUNT = 1
    }
}
