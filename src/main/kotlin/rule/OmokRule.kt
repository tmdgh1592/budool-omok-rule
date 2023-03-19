package rule

import rule.type.Foul
import rule.type.KoRule
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
     * The function will return True if any of the three forbidden moves '3-3', '4-4', and 'overline' is detected.
     * @param blackPoints List of pairs for row and column of black stones.
     * @param whitePoints List of pairs for row and column of white stones.
     * @param startPoint The row and column of the stone that is being placed.
     * @return The result of checking all numbers.
     * */
    fun checkAll(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
    ): KoRule = if (listOf(
            checkFoul(blackPoints, whitePoints, startPoint, Foul.DOUBLE_THREE),
            checkFoul(blackPoints, whitePoints, startPoint, Foul.DOUBLE_FOUR),
            checkOverline(blackPoints, startPoint)
        ).any { it.state }
    ) KoRule.KO_ALL else KoRule.NOT_KO


    /**
     * check 'three-three' point or 'four-four' point according to the given 'foul type'
     * @param blackPoints List of pairs for row and column of black stones.
     * @param whitePoints List of pairs for row and column of white stones.
     * @param startPoint The row and column of the stone that is being placed.
     * @return Whether the given row and column correspond to 3-3 or 4-4 according to the given 'foul type'.
     * */
    abstract fun checkFoul(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        foul: Foul,
    ): KoRule


    /**
     * Check 'overline' pattern.
     * @param stonesPoints List of stone points for the given row and column to check for overline.
     * @param startPoint The row and column of the stone that is being placed.
     * @return Boolean value indicating whether it is overline.
     * */
    abstract fun checkOverline(
        stonesPoints: List<Point>,
        startPoint: Point,
    ): KoRule

    protected infix fun List<Point>.isPlaced(point: Point): Boolean = contains(point)
}
