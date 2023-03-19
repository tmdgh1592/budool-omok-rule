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
            Position(3, 3),
            Position(3, 4),
            Position(4, 4),
            Position(5, 3),
            Position(12, 3),
            Position(12, 5),
            Position(13, 4),
            Position(14, 4),
            Position(6, 2),
            Position(5, 5),
            Position(6, 5),
            Position(3, 11),
            Position(6, 11),
            Position(4, 13),
            Position(4, 14),
            Position(9, 14),
            Position(10, 13),
            Position(12, 13),
            Position(9, 10),
        )
        val whiteStones = listOf(Position(9, 9))
        val newStone = Position(3, 5)

        val expected = renjuRule.checkThreeToThreePoint(blackStones, whiteStones, newStone)
        assertThat(expected).isEqualTo(KoRule.KO_THREE_TO_THREE)
    }

    @ParameterizedTest
    @CsvSource("13, 3", "8, 3", "12, 6", "10, 10", "8, 9", "5, 8")
    fun `4-4 테스트`(newStoneRow: Int, newStoneCol: Int) {
        val blackStones = listOf(
            Position(15, 3),
            Position(14, 3),
            Position(12, 3),
            Position(11, 3),
            Position(10, 3),
            Position(12, 4),
            Position(12, 7),
            Position(12, 9),
            Position(12, 10),
            Position(9, 10),
            Position(8, 10),
            Position(6, 10),
            Position(8, 11),
            Position(8, 8),
            Position(7, 8),
            Position(6, 8),
            Position(6, 5),
            Position(5, 5),
            Position(5, 6),
            Position(5, 7),
            Position(4, 7),
        )
        val whiteStones = listOf(
            Position(5, 4),
            Position(9, 8),
        )
        val newStone = Position(newStoneRow, newStoneCol)

        val expected = renjuRule.checkFourToFourPoint(blackStones, whiteStones, newStone)
        assertThat(expected).isEqualTo(KoRule.KO_FOUR_TO_FOUR)
    }

    @Test
    fun `장목 테스트`() {
        val blackStones = listOf(
            Position(5, 5),
            Position(6, 6),
            Position(7, 7),
            Position(9, 9),
            Position(10, 10),
        )
        val newStone = Position(8, 8)

        val expected = renjuRule.checkOverline(blackStones, newStone)
        assertThat(expected).isEqualTo(KoRule.KO_OVERLINE)
    }
}
