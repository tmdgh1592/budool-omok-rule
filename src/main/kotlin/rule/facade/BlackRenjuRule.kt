package rule.facade

import rule.BlackRenjuRuleImpl
import rule.type.Foul
import rule.type.Violation
import rule.wrapper.point.Point

class BlackRenjuRule(
    boardWidth: Int,
    boardHeight: Int,
) {
    private val rule = BlackRenjuRuleImpl(boardWidth, boardHeight)

    fun checkWin(
        blackPoints: List<Pair<Int, Int>>,
        whitePoints: List<Pair<Int, Int>>,
        startPoint: Pair<Int, Int>,
        stoneStandardCount: Int,
    ): Boolean {
        return rule.checkWin(
            blackPoints.map { it.toPoint() },
            whitePoints.map { it.toPoint() },
            startPoint.toPoint(),
            stoneStandardCount
        )
    }

    fun checkDoubleFourFoul(
        blackPoints: List<Pair<Int, Int>>,
        whitePoints: List<Pair<Int, Int>>,
        startPoint: Pair<Int, Int>,
    ): Boolean {
        return rule.checkDoubleFoul(
            blackPoints.map { it.toPoint() },
            whitePoints.map { it.toPoint() },
            startPoint.toPoint(),
            Foul.DOUBLE_FOUR
        ) == Violation.DOUBLE_FOUR
    }

    fun checkDoubleThreeFoul(
        blackPoints: List<Pair<Int, Int>>,
        whitePoints: List<Pair<Int, Int>>,
        startPoint: Pair<Int, Int>,
    ): Boolean {
        return rule.checkDoubleFoul(
            blackPoints.map { it.toPoint() },
            whitePoints.map { it.toPoint() },
            startPoint.toPoint(),
            Foul.DOUBLE_THREE
        ) == Violation.DOUBLE_THREE
    }

    fun checkOverline(
        stonesPoints: List<Pair<Int, Int>>,
        startPoint: Pair<Int, Int>,
    ): Boolean {
        return rule.checkOverline(
            stonesPoints.map { it.toPoint() },
            startPoint.toPoint()
        ) == Violation.OVERLINE
    }

    private fun Pair<Int, Int>.toPoint(): Point {
        return Point(first, second)
    }
}
