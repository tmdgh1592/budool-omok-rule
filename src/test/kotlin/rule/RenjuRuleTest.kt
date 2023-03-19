package rule

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

        val expected = renjuRule.checkFoul(blackStones, whiteStones, newStone, FoulType.THREE_TO_THREE)
        assertThat(expected).isEqualTo(KoRule.KO_THREE_TO_THREE)
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

        val expected = renjuRule.checkFoul(blackStones, whiteStones, newStone, FoulType.FOUR_TO_FOUR)
        assertThat(expected).isEqualTo(KoRule.KO_FOUR_TO_FOUR)
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

        val expected = renjuRule.checkOverline(blackStones, newStone)
        assertThat(expected).isEqualTo(KoRule.KO_OVERLINE)
    }
}
