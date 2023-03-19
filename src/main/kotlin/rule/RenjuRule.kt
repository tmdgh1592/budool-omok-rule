package rule

class RenjuRule(
    boardWidth: Int = 15,
    boardHeight: Int = 15
) : OmokRule(boardWidth, boardHeight) {
    override fun checkThreeToThreePoint(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        return check33AllDirections(blackPositions, whitePositions, startPosition)
        // blackPlayer에서 돌을 놓고 이 규칙을 확인한다면, 이미 blackStones에는 돌이 들어간 상태니까 startStone을 삭제하고 blackStones.getLastStone으로 해결할 수 있을 것 같음!
        // "금지된 수를 놓으면서 동시에 5도 만들어지는 경우에는 흑 승리로 인정된다." -> 놓고나서 이겼는지 체크하고, 이기지도 않았는데 금수인 경우에는 패배로 바꿔야할 것 같음
    }

    override fun checkFourToFourPoint(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        return check44AllDirections(blackPositions, whitePositions, startPosition)
    }

    override fun checkOverline(
        stonesPositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        for (moveDirection in directions) {
            val forwardCount = findLongOmok(stonesPositions, startPosition, moveDirection, FORWARD_WEIGHT)
            val backCount = findLongOmok(stonesPositions, startPosition, moveDirection, BACK_WEIGHT)

            if (forwardCount + backCount - 1 > 5) return KoRule.KO_OVERLINE
        }
        return KoRule.NOT_KO
    }

    private fun check33AllDirections(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        var threeCount = 0

        for (moveDirection in directions) {
            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                FORWARD_WEIGHT,
                3
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                BACK_WEIGHT,
                3
            )

            // 만약 빈 칸이 2 미만이고, 같은 돌 개수가 무조건 3이면 3-3 가능성 ok
            if (forwardCount + backCount - 1 == 3 && forwardEmptyCount + backEmptyCount <= 1) {
                // 백돌 양쪽 합 6칸 이내에 2개 이상 있는지 확인한다.
                // 닫혀 있으면 다른 방향 확인
                if (!isBlockedByWhiteStoneInSix(whitePositions, startPosition, moveDirection)) threeCount++
                if (threeCount == 2) return KoRule.KO_THREE_TO_THREE
            }
        }
        return KoRule.NOT_KO
    }

    private fun check44AllDirections(
        blackPositions: List<Position<Row, Col>>,
        whitePositions: List<Position<Row, Col>>,
        startPosition: Position<Row, Col>,
    ): KoRule {
        var fourCount = 0
        for (moveDirection in directions) {
            val (forwardCount, forwardEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                FORWARD_WEIGHT,
                4
            )
            val (backCount, backEmptyCount) = findStraight(
                blackPositions,
                whitePositions,
                startPosition,
                moveDirection,
                BACK_WEIGHT,
                4
            )
            // 만약 빈 칸이 2 미만이고, 같은 돌 개수가 무조건 4이면 4-4 가능성 ok
            val stoneCount = forwardCount + backCount - 1
            // 1자 4-4
            if (stoneCount >= 5 && forwardEmptyCount == 1 && backEmptyCount == 1) return KoRule.KO_FOUR_TO_FOUR
            // 각각 다른 방향 4-4
            if (stoneCount == 4 && forwardEmptyCount + backEmptyCount <= 1) {
                fourCount++
                if (fourCount == 2) return KoRule.KO_FOUR_TO_FOUR
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
        return oneDirMoveCount + otherDirMoveCount <= 6 && oneDirFound && otherDirFound
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
        while (inRange(curRow, curCol) && moveCount <= 6) {
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
        var sameStoneCount = 1
        var emptyCount = 0
        var (currentRow, currentCol) = Pair(startRow + direction.first * weight, startCol + direction.second * weight)

        // 현재 탐색 방향에
        // 흰 돌이 아니고, 범위 안에 있고
        // 같은 돌의 개수가 stoneCount개 이하이고, 공백이 1개 이하일 때까지
        while (inRange(
                currentRow,
                currentCol
            ) && emptyCount <= 1 && sameStoneCount < stoneCount && !whitePositions.isPlaced(currentRow, currentCol)
        ) {
            val hasBlackStone = blackPositions.isPlaced(currentRow, currentCol)
            val hasWhiteStone = whitePositions.isPlaced(currentRow, currentCol)
            val isEmpty = !hasBlackStone && !hasWhiteStone
            // 검은 돌이 있는지 확인한다.
            if (hasBlackStone) ++sameStoneCount
            // 빈 칸인지 확인한다.
            if (isEmpty) ++emptyCount
            currentRow += direction.first * weight
            currentCol += direction.second * weight
        }
        currentRow -= direction.first * weight
        currentCol -= direction.second * weight
        // 필요없는 빈칸 개수 빼기
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
        var sameStoneCount = 1
        var (currentRow, currentCol) = Pair(startRow + direction.first * weight, startCol + direction.second * weight)

        while (inRange(currentRow, currentCol) && stonesPositions.isPlaced(currentRow, currentCol)) {
            // 검은 돌이 있는지 확인한다.
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
    }
}
