package rule

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import rule.type.Foul
import rule.type.KoRule
import rule.wrapper.point.Point

class RenjuRuleTest {
    private lateinit var renjuRule: OmokRule

    @BeforeEach
    fun setUp() {
        renjuRule = RenjuRule(15, 15)
    }

    @ParameterizedTest
    @CsvSource("3, 5", "12, 4", "3, 5", "4, 11", "11, 12")
    fun `3-3 테스트`() {
        val blackStones = listOf(
            Point(3, 3),
            Point(3, 4),
            Point(4, 4),
            Point(5, 3),
            Point(12, 3),
            Point(12, 5),
            Point(13, 4),
            Point(14, 4),
            Point(6, 2),
            Point(5, 5),
            Point(6, 5),
            Point(3, 11),
            Point(6, 11),
            Point(4, 13),
            Point(4, 14),
            Point(9, 14),
            Point(10, 13),
            Point(12, 13),
            Point(9, 10),
        )
        val whiteStones = listOf(Point(9, 9))
        val newStone = Point(3, 5)
        val isBlackStone = true

        val expected = renjuRule.checkFoul(blackStones, whiteStones, newStone, Foul.DOUBLE_THREE, isBlackStone)
        assertThat(expected).isEqualTo(KoRule.KO_DOUBLE_THREE)
    }

    @ParameterizedTest
    @CsvSource("8, 3", "12, 6", "10, 10", "8, 9", "5, 8")
    fun `4-4 테스트`(newStoneRow: Int, newStoneCol: Int) {
        val blackStones = listOf(
            Point(15, 3),
            Point(14, 3),
            Point(12, 3),
            Point(11, 3),
            Point(10, 3),
            Point(12, 4),
            Point(12, 7),
            Point(12, 9),
            Point(12, 10),
            Point(9, 10),
            Point(8, 10),
            Point(6, 10),
            Point(8, 11),
            Point(8, 8),
            Point(7, 8),
            Point(6, 8),
            Point(6, 5),
            Point(5, 5),
            Point(5, 6),
            Point(5, 7),
            Point(4, 7),
        )
        val whiteStones = listOf(
            Point(5, 4),
            Point(9, 8),
        )
        val newStone = Point(newStoneRow, newStoneCol)
        val isBlackStone = true

        val expected = renjuRule.checkFoul(blackStones, whiteStones, newStone, Foul.DOUBLE_FOUR, isBlackStone)
        assertThat(expected).isEqualTo(KoRule.KO_DOUBLE_FOUR)
    }

    @Test
    fun `장목 테스트`() {
        val blackStones = listOf(
            Point(5, 5),
            Point(6, 6),
            Point(7, 7),
            Point(9, 9),
            Point(10, 10),
        )
        val newStone = Point(8, 8)
        val isBlackStone = true

        val expected = renjuRule.checkOverline(blackStones, newStone, isBlackStone)
        assertThat(expected).isEqualTo(KoRule.KO_OVERLINE)
    }

    @Test
    fun `만약 5개가 연이어져 있다면 3-3, 4-4어도 반칙이 아니다`() {
        val blackStones = listOf(
            Point(5, 5),
            Point(5, 6),
            Point(6, 7),
            Point(7, 7),
            Point(5, 8),
            Point(5, 9),
        )
        val whiteStones = listOf<Point>()
        val newStone = Point(5, 7)
        val isBlackStone = true

        val expected = renjuRule.checkAllFoulCondition(blackStones, whiteStones, newStone, isBlackStone)
        assertThat(expected).isEqualTo(KoRule.NOT_KO)
    }
}
