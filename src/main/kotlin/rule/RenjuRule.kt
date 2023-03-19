package rule

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
    ): KoRule {
        return checkAllFourToFourDirections(blackPositions, whitePositions, startPosition)
    }

    override fun checkOverline(
        stonesPositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        for (moveDirection in directions) {
            val forwardCount = findLongOmok(stonesPositions, startPosition, moveDirection, FORWARD_WEIGHT)
            val backCount = findLongOmok(stonesPositions, startPosition, moveDirection, BACK_WEIGHT)

            if (forwardCount + backCount > MAX_DISTANCE_TWO_BLOCKED_WHITE_STONE) return KoRule.KO_OVERLINE
        }
        return KoRule.NOT_KO
    }

    private fun checkAllThreeToThreeDirections(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        var threeCount = DEFAULT_SAME_STONE_COUNT

        for (moveDirection in directions) {
            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                FORWARD_WEIGHT,
                THREE_TO_THREE_SIZE,
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                BACK_WEIGHT,
                THREE_TO_THREE_SIZE,
            )

            // 만약 빈 칸이 2 미만이고, 같은 돌 개수가 무조건 3이면 3-3 가능성 ok
            if (forwardCount + backCount - 1 == THREE_TO_THREE_SIZE && forwardEmptyCount + backEmptyCount <= MAX_EMPTY_SIZE) {
                // 백돌 양쪽 합 6칸 이내에 2개 이상 있는지 확인한다.
                // 닫혀 있으면 다른 방향 확인
                if (!isBlockedByWhiteStoneInSix(whitePositions, startPosition, moveDirection)) threeCount++
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
        var fourCount = DEFAULT_SAME_STONE_COUNT
        for (moveDirection in directions) {
            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                FORWARD_WEIGHT,
                FOUR_TO_FOUR_SIZE,
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                BACK_WEIGHT,
                FOUR_TO_FOUR_SIZE,
            )
            // 만약 빈 칸이 2 미만이고, 같은 돌 개수가 무조건 4이면 4-4 가능성 ok
            val stoneCount = forwardCount + backCount - 1
            // 1자 4-4
            if (stoneCount >= 5 && forwardEmptyCount == 1 && backEmptyCount == 1) return KoRule.KO_FOUR_TO_FOUR
            // 각각 다른 방향 4-4
            if (stoneCount == FOUR_TO_FOUR_SIZE && forwardEmptyCount + backEmptyCount <= MAX_EMPTY_SIZE) {
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
    ): Boolean {
        val (oneDirMoveCount, oneDirFound) = checkWhite(
            whitePositions,
            position,
            direction,
            FORWARD_WEIGHT,
        )
        val (otherDirMoveCount, otherDirFound) = checkWhite(
            whitePositions,
            position,
            direction,
            BACK_WEIGHT,
        )
        // 양 방향 6칸 이하에 각각 1개씩 있으면 참
        return oneDirMoveCount + otherDirMoveCount <= MAX_DISTANCE_TWO_BLOCKED_WHITE_STONE &&
                oneDirFound && otherDirFound
    }

    private fun checkWhite(
        whiteStones: List<Position<Row, Col>>,
        position: Position<Row, Col>,
        direction: Direction<Row, Col>,
        weight: MoveWeight,
    ): Pair<Int, Boolean> {
        var (curRow, curCol) = Pair(
            position.first + direction.first * weight, position.second + direction.second * weight
        )
        var moveCount = 0
        while (inRange(curRow, curCol) && moveCount <= MAX_DISTANCE_TWO_BLOCKED_WHITE_STONE) {
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
        weight: Int = FORWARD_WEIGHT,
        stoneCount: Int,
    ): Pair<Int, Int> {
        val (startRow, startCol) = startPosition
        var sameStoneCount = DEFAULT_SAME_STONE_COUNT
        var emptyCount = DEFAULT_EMPTY_COUNT
        var (currentRow, currentCol) = Pair(startRow + direction.first * weight, startCol + direction.second * weight)

        while (inRange(
                currentRow,
                currentCol
        ) && emptyCount <= MAX_EMPTY_SIZE && sameStoneCount < stoneCount && !whitePositions.isPlaced(currentRow, currentCol)
        ) {
            val hasBlackStone = blackPositions.isPlaced(currentRow, currentCol)
            val hasWhiteStone = whitePositions.isPlaced(currentRow, currentCol)
            val isEmpty = !hasBlackStone && !hasWhiteStone
            if (hasBlackStone) ++sameStoneCount
            if (isEmpty) ++emptyCount
            currentRow += direction.first * weight
            currentCol += direction.second * weight
        }
        currentRow -= direction.first * weight
        currentCol -= direction.second * weight
        while ((startRow != currentRow || startCol != currentCol) && !blackPositions.isPlaced(currentRow, currentCol)) {
            emptyCount -= 1
            currentRow -= direction.first * weight
            currentCol -= direction.second * weight
        }
        return Pair(sameStoneCount, emptyCount)
    }

    private fun findLongOmok(
        stonesPositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
        direction: Pair<Int, Int>,
        weight: Int = FORWARD_WEIGHT,
    ): Int {
        val (startRow, startCol) = startPosition
        var sameStoneCount = DEFAULT_SAME_STONE_COUNT
        var (currentRow, currentCol) = Pair(startRow + direction.first * weight, startCol + direction.second * weight)

        while (inRange(currentRow, currentCol) && stonesPositions.isPlaced(currentRow, currentCol)) {
            sameStoneCount++
            currentRow += direction.first * weight
            currentCol += direction.second * weight
        }
        return sameStoneCount
    }

    companion object {
        private val RIGHT_DIRECTION = Pair(1, 0)
        private val TOP_DIRECTION = Pair(0, 1)
        private val RIGHT_TOP_DIRECTION = Pair(1, 1)
        private val LEFT_BOTTOM_DIRECTION = Pair(-1, 1)
        private val directions = listOf(RIGHT_DIRECTION, TOP_DIRECTION, RIGHT_TOP_DIRECTION, LEFT_BOTTOM_DIRECTION)

        private const val FORWARD_WEIGHT = 1
        private const val BACK_WEIGHT = -1

        private const val MAX_DISTANCE_TWO_BLOCKED_WHITE_STONE = 6

        private const val DEFAULT_BOARD_WIDTH = 15
        private const val DEFAULT_BOARD_HEIGHT = 15

        private const val THREE_TO_THREE_SIZE = 3
        private const val FOUR_TO_FOUR_SIZE = 4
        private const val FOUL_CONDITION_SIZE = 2
        private const val MAX_EMPTY_SIZE = 1

        private const val DEFAULT_SAME_STONE_COUNT = 1
        private const val DEFAULT_EMPTY_COUNT = 0
    }
}
