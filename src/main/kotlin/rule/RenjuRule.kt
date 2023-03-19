package rule

import rule.KoRule.Companion.FOUL_CONDITION_SIZE
import rule.KoRule.Companion.MAX_EMPTY_SIZE
import rule.KoRule.Companion.OVERLINE_SIZE

class RenjuRule(
    boardWidth: Int = DEFAULT_BOARD_WIDTH,
    boardHeight: Int = DEFAULT_BOARD_HEIGHT,
) : OmokRule(boardWidth, boardHeight) {

    override fun checkFoul(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
        foulType: FoulType,
    ): KoRule = checkFoulByAllDirections(blackPositions, whitePositions, startPosition, foulType)


    override fun checkOverline(
        stonesPositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        val dirIterator = Directions().iterator()

        while (dirIterator.hasNext()) {
            val forwardCount = findLongOmok(stonesPositions, startPosition, dirIterator.next())
            val backCount = findLongOmok(stonesPositions, startPosition, dirIterator.next())
            val totalMoveCount = forwardCount + backCount - 1
            if (totalMoveCount >= OVERLINE_SIZE) return KoRule.KO_OVERLINE
        }
        return KoRule.NOT_KO
    }


    private fun checkFoulByAllDirections(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
        foulType: FoulType,
    ): KoRule {
        var continuousStones = 0
        val dirIterator = Directions().iterator()

        while (dirIterator.hasNext()) {
            val forwardDir = dirIterator.next()
            val backDir = dirIterator.next()

            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions, whitePositions,
                startPosition, forwardDir,
                foulType,
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions, whitePositions,
                startPosition, backDir,
                foulType,
            )
            val totalStoneCount = forwardCount + backCount - 1
            val totalEmptyCount = forwardEmptyCount + backEmptyCount

            when (foulType) {
                FoulType.THREE_TO_THREE -> {
                    if (totalStoneCount == foulType.size && totalEmptyCount <= MAX_EMPTY_SIZE) {
                        val blockedStatus = isBlockedByWhiteStoneInSix(whitePositions, startPosition, forwardDir)
                        if (blockedStatus == WhiteBlockedStatus.NON_BLOCK) continuousStones++
                        if (continuousStones == FOUL_CONDITION_SIZE) return KoRule.KO_THREE_TO_THREE
                    }
                }

                FoulType.FOUR_TO_FOUR -> {
                    if (totalStoneCount > foulType.size && forwardEmptyCount == 1 && backEmptyCount == 1) return KoRule.KO_FOUR_TO_FOUR
                    if (totalStoneCount == foulType.size && totalEmptyCount <= MAX_EMPTY_SIZE) continuousStones++
                    if (continuousStones == FOUL_CONDITION_SIZE) return KoRule.KO_FOUR_TO_FOUR
                }
            }

        }
        return KoRule.NOT_KO
    }


    private fun isBlockedByWhiteStoneInSix(
        whitePositions: List<Position<Row, Col>>,
        position: Position<Row, Col>,
        direction: Direction<Row, Col>,
    ): WhiteBlockedStatus {
        val (oneDirMoveCount, oneDirFound) = checkWhite(
            whitePositions, position,
            direction, FORWARD_WEIGHT,
        )
        val (otherDirMoveCount, otherDirFound) = checkWhite(
            whitePositions, position,
            direction, BACK_WEIGHT,
        )
        val totalMoveCount = oneDirMoveCount + otherDirMoveCount
        return WhiteBlockedStatus.of(
            totalMoveCount <= WhiteBlockedStatus.INNER_DISTANCE &&
                    oneDirFound && otherDirFound
        )
    }


    private fun checkWhite(
        whiteStones: List<Position<Row, Col>>,
        position: Position<Row, Col>,
        direction: Direction<Row, Col>,
        weight: MoveWeight,
    ): Pair<Int, Boolean> {
        var (curRow, curCol) = Pair(
            position.first + direction.first * weight,
            position.second + direction.second * weight
        )
        var moveCount = 0
        while (inRange(curRow, curCol) && moveCount <= WhiteBlockedStatus.INNER_DISTANCE) {
            moveCount++
            if (whiteStones.isPlaced(curRow, curCol)) return Pair(moveCount, true)
            curRow += direction.first * weight
            curCol += direction.second * weight
        }
        return Pair(moveCount, false)
    }


    private fun findStraight(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
        direction: Pair<Int, Int>,
        foulType: FoulType,
    ): Pair<Int, Int> {
        val (startRow, startCol) = startPosition
        var sameStoneCount = DEFAULT_SAME_STONE_COUNT
        var emptyCount = DEFAULT_EMPTY_COUNT
        var (currentRow, currentCol) = Pair(startRow + direction.first, startCol + direction.second)

        while (inRange(currentRow, currentCol) &&
            !whitePositions.isPlaced(currentRow, currentCol) &&
            emptyCount <= MAX_EMPTY_SIZE &&
            sameStoneCount < foulType.size
        ) {
            val hasBlackStone = blackPositions.isPlaced(currentRow, currentCol)
            val hasWhiteStone = whitePositions.isPlaced(currentRow, currentCol)
            val isEmpty = !hasBlackStone && !hasWhiteStone
            if (hasBlackStone) ++sameStoneCount
            if (isEmpty) ++emptyCount
            currentRow += direction.first
            currentCol += direction.second
        }
        currentRow -= direction.first
        currentCol -= direction.second
        while ((startRow != currentRow || startCol != currentCol) && !blackPositions.isPlaced(currentRow, currentCol)) {
            emptyCount -= 1
            currentRow -= direction.first
            currentCol -= direction.second
        }
        return Pair(sameStoneCount, emptyCount)
    }


    private fun findLongOmok(
        stonesPositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
        direction: Pair<Int, Int>,
    ): Int {
        val (startRow, startCol) = startPosition
        var sameStoneCount = DEFAULT_SAME_STONE_COUNT
        var (currentRow, currentCol) = Pair(startRow + direction.first, startCol + direction.second)

        while (inRange(currentRow, currentCol) && stonesPositions.isPlaced(currentRow, currentCol)) {
            sameStoneCount++
            currentRow += direction.first
            currentCol += direction.second
        }
        return sameStoneCount
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
