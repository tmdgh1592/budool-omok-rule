package rule

import rule.type.Foul
import rule.type.Violation
import rule.type.Violation.Companion.FOUL_CONDITION_SIZE
import rule.type.Violation.Companion.MAX_EMPTY_SIZE
import rule.type.Violation.Companion.OVERLINE_SIZE
import rule.type.WhiteBlocked
import rule.wrapper.direction.Directions
import rule.wrapper.point.Point

internal class BlackRenjuRuleImpl(
    boardWidth: Int = DEFAULT_BOARD_WIDTH,
    boardHeight: Int = DEFAULT_BOARD_HEIGHT,
) : OmokRule(boardWidth, boardHeight) {

    override fun checkDoubleFoul(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        foul: Foul,
    ): Violation = checkFoulByAllDirections(blackPoints, whitePoints, startPoint, foul)

    override fun checkOverline(
        stonesPoints: List<Point>,
        startPoint: Point,
    ): Violation {
        if (checkSerialSameStonesBiDirection(stonesPoints, startPoint, OVERLINE_SIZE)) return Violation.OVERLINE
        return Violation.NONE
    }

    private fun checkFoulByAllDirections(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        foul: Foul,
    ): Violation {
        var continuousStones = 0
        val dirIterator = Directions().iterator()

        while (dirIterator.hasNext()) {
            val forwardDir = dirIterator.next()
            val backDir = dirIterator.next()

            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPoints, whitePoints, startPoint,
                forwardDir, foul,
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPoints, whitePoints, startPoint,
                backDir, foul,
            )
            val totalStoneCount = forwardCount + backCount - 1
            val totalEmptyCount = forwardEmptyCount + backEmptyCount

            when (foul) {
                Foul.DOUBLE_THREE -> {
                    if (totalStoneCount == foul.size && totalEmptyCount <= MAX_EMPTY_SIZE) {
                        val blockedStatus = isBlockedByWhiteStoneInSix(whitePoints, startPoint, forwardDir)
                        if (blockedStatus == WhiteBlocked.NON_BLOCK) continuousStones++
                        if (continuousStones == FOUL_CONDITION_SIZE) return Violation.DOUBLE_THREE
                    }
                }

                Foul.DOUBLE_FOUR -> {
                    if (totalStoneCount > foul.size && forwardEmptyCount == 1 && backEmptyCount == 1) return Violation.DOUBLE_FOUR
                    if (totalStoneCount == foul.size && totalEmptyCount <= MAX_EMPTY_SIZE) continuousStones++
                    if (continuousStones == FOUL_CONDITION_SIZE) return Violation.DOUBLE_FOUR
                }
            }
        }
        return Violation.NONE
    }

    private fun isBlockedByWhiteStoneInSix(
        whitePoints: List<Point>,
        point: Point,
        direction: Direction<Row, Col>,
    ): WhiteBlocked {
        val (oneDirMoveCount, oneDirFound) = checkWhite(
            whitePoints, point,
            direction, FORWARD_WEIGHT,
        )
        val (otherDirMoveCount, otherDirFound) = checkWhite(
            whitePoints, point,
            direction, BACK_WEIGHT,
        )
        val totalMoveCount = oneDirMoveCount + otherDirMoveCount
        return WhiteBlocked.of(
            totalMoveCount <= WhiteBlocked.INNER_DISTANCE &&
                oneDirFound && otherDirFound
        )
    }

    private fun checkWhite(
        whiteStones: List<Point>,
        point: Point,
        direction: Direction<Row, Col>,
        weight: MoveWeight,
    ): Pair<Int, Boolean> {
        val rowStep = direction.first * weight
        val colStep = direction.second * weight
        var curPoint = point.move(rowStep, colStep)
        var moveCount = 0
        while (curPoint.inRange(boardWidth, boardHeight) && moveCount <= WhiteBlocked.INNER_DISTANCE) {
            moveCount++
            if (whiteStones isPlaced curPoint) return Pair(moveCount, true)
            curPoint = curPoint.move(rowStep, colStep)
        }
        return Pair(moveCount, false)
    }

    private fun findStraight(
        blackPoints: List<Point>,
        whitePoints: List<Point>,
        startPoint: Point,
        direction: Direction<Row, Col>,
        foul: Foul,
    ): Pair<Int, Int> {
        var sameStoneCount = DEFAULT_SAME_STONE_COUNT
        var emptyCount = DEFAULT_EMPTY_COUNT
        val rowStep = direction.first
        val colStep = direction.second
        var curPoint = startPoint.move(rowStep, colStep)

        while (curPoint.inRange(boardWidth, boardHeight) &&
            !whitePoints.isPlaced(curPoint) &&
            emptyCount <= MAX_EMPTY_SIZE &&
            sameStoneCount < foul.size
        ) {
            val hasBlackStone = blackPoints isPlaced curPoint
            val hasWhiteStone = whitePoints isPlaced curPoint
            val isEmpty = !hasBlackStone && !hasWhiteStone
            if (hasBlackStone) ++sameStoneCount
            if (isEmpty) ++emptyCount
            curPoint = curPoint.move(rowStep, colStep)
        }
        curPoint = curPoint.move(-rowStep, -colStep)
        while (startPoint != curPoint && !(blackPoints isPlaced curPoint)) {
            emptyCount -= 1
            curPoint = curPoint.move(-rowStep, -colStep)
        }
        return Pair(sameStoneCount, emptyCount)
    }

    companion object {
        private const val FORWARD_WEIGHT = 1
        private const val BACK_WEIGHT = -1

        private const val DEFAULT_BOARD_WIDTH = 15
        private const val DEFAULT_BOARD_HEIGHT = 15

        private const val DEFAULT_SAME_STONE_COUNT = 1
        private const val DEFAULT_EMPTY_COUNT = 0
    }
}
