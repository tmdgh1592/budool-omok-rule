package rule

import rule.KoRule.Companion.FOUL_CONDITION_SIZE
import rule.KoRule.Companion.MAX_EMPTY_SIZE
import rule.KoRule.Companion.OVERLINE_SIZE

class RenjuRule(
    boardWidth: Int = DEFAULT_BOARD_WIDTH,
    boardHeight: Int = DEFAULT_BOARD_HEIGHT,
) : OmokRule(boardWidth, boardHeight) {

    override fun checkThreeToThreePoint(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule = checkAllThreeToThreeDirections(blackPositions, whitePositions, startPosition)


    override fun checkFourToFourPoint(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule = checkAllFourToFourDirections(blackPositions, whitePositions, startPosition)


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


    private fun checkAllThreeToThreeDirections(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        var threeCount = 0
        val dirIterator = Directions().iterator()
        val forwardDir = dirIterator.next()
        while (dirIterator.hasNext()) {
            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions, whitePositions,
                startPosition, forwardDir,
                FoulType.THREE_TO_THREE,
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions, whitePositions,
                startPosition, dirIterator.next(),
                FoulType.THREE_TO_THREE,
            )

            if (forwardCount + backCount - 1 == FoulType.THREE_TO_THREE.size &&
                forwardEmptyCount + backEmptyCount <= MAX_EMPTY_SIZE
            ) {
                val blockedStatus = isBlockedByWhiteStoneInSix(whitePositions, startPosition, forwardDir)
                if (blockedStatus == WhiteBlockedStatus.NON_BLOCK) threeCount++
                if (threeCount == FOUL_CONDITION_SIZE) return KoRule.KO_THREE_TO_THREE
            }
        }
        return KoRule.NOT_KO
    }


    private fun checkAllFourToFourDirections(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        var fourCount = 0
        val dirIterator = Directions().iterator()

        while (dirIterator.hasNext()) {
            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions, whitePositions,
                startPosition, dirIterator.next(),
                FoulType.FOUR_TO_FOUR,
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions, whitePositions,
                startPosition, dirIterator.next(),
                FoulType.FOUR_TO_FOUR,
            )

            val stoneCount = forwardCount + backCount - 1
            if (stoneCount >= 5 && forwardEmptyCount == 1 && backEmptyCount == 1) return KoRule.KO_FOUR_TO_FOUR
            if (stoneCount == FoulType.FOUR_TO_FOUR.size && forwardEmptyCount + backEmptyCount <= MAX_EMPTY_SIZE) {
                fourCount++
                if (fourCount == FOUL_CONDITION_SIZE) return KoRule.KO_FOUR_TO_FOUR
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
