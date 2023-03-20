package rule

import rule.type.Foul
import rule.type.KoRule
import rule.wrapper.point.Point

class WhiteRenjuRule(
    boardWidth: Int = DEFAULT_BOARD_WIDTH,
    boardHeight: Int = DEFAULT_BOARD_HEIGHT,
) : OmokRule(boardWidth, boardHeight) {
    override fun checkDoubleFoul(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        foul: Foul,
    ): KoRule = KoRule.NOT_KO

    override fun checkOverline(
        stonesPoints: List<Point>,
        startPoint: Point,
    ): KoRule = KoRule.NOT_KO

    companion object {
        private const val DEFAULT_BOARD_WIDTH = 15
        private const val DEFAULT_BOARD_HEIGHT = 15
    }
}
