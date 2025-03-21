package rule

import rule.type.Foul
import rule.type.Violation
import rule.wrapper.point.Point

internal class WhiteRenjuRuleImpl(
    boardWidth: Int = DEFAULT_BOARD_WIDTH,
    boardHeight: Int = DEFAULT_BOARD_HEIGHT,
) : OmokRule(boardWidth, boardHeight) {
    override fun checkDoubleFoul(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        foul: Foul,
    ): Violation = Violation.NONE

    override fun checkOverline(
        stonesPoints: List<Point>,
        startPoint: Point,
    ): Violation = Violation.NONE

    companion object {
        private const val DEFAULT_BOARD_WIDTH = 15
        private const val DEFAULT_BOARD_HEIGHT = 15
    }
}
