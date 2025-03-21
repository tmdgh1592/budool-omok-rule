package rule

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import rule.facade.BlackRenjuRule

class BlackRenjuRuleTest {
    private lateinit var renjuRule: BlackRenjuRule

    @BeforeEach
    fun setUp() {
        renjuRule = BlackRenjuRule(15, 15)
    }

    @ParameterizedTest
    @CsvSource("3, 5", "12, 4", "3, 5", "4, 11", "11, 12")
    fun `A black stone is a foul if it is double three`() {
        // given
        val blackStones = listOf(
            Pair(3, 3), Pair(3, 4), Pair(4, 4),
            Pair(5, 3), Pair(12, 3), Pair(12, 5),
            Pair(13, 4), Pair(14, 4), Pair(6, 2),
            Pair(5, 5), Pair(6, 5), Pair(3, 11),
            Pair(6, 11), Pair(4, 13), Pair(4, 14),
            Pair(9, 14), Pair(10, 13), Pair(12, 13),
            Pair(9, 10),
        )
        val whiteStones = listOf(Pair(9, 9))
        val newStone = Pair(3, 5)

        // when
        val expected = renjuRule.checkDoubleThreeFoul(blackStones, whiteStones, newStone)

        // then
        assertThat(expected).isEqualTo(true)
    }

    @ParameterizedTest
    @CsvSource("8, 3", "12, 6", "10, 10", "8, 9", "5, 8")
    fun `A black stone is a foul if it is double four`(newStoneRow: Int, newStoneCol: Int) {
        // given
        val blackStones = listOf(
            Pair(15, 3), Pair(14, 3), Pair(12, 3),
            Pair(11, 3), Pair(10, 3), Pair(12, 4),
            Pair(12, 7), Pair(12, 9), Pair(12, 10),
            Pair(9, 10), Pair(8, 10), Pair(6, 10),
            Pair(8, 11), Pair(8, 8), Pair(7, 8),
            Pair(6, 8), Pair(6, 5), Pair(5, 5),
            Pair(5, 6), Pair(5, 7), Pair(4, 7),
        )
        val whiteStones = listOf(
            Pair(5, 4),
            Pair(9, 8),
        )
        val newStone = Pair(newStoneRow, newStoneCol)

        // when
        val expected = renjuRule.checkDoubleFourFoul(blackStones, whiteStones, newStone)

        assertThat(expected).isEqualTo(true)
    }

    @Test
    fun `Black stone is a foul in the case of overline`() {
        // given
        val blackStones = listOf(
            Pair(5, 5),
            Pair(6, 6),
            Pair(7, 7),
            Pair(9, 9),
            Pair(10, 10),
        )
        val newStone = Pair(8, 8)

        // when
        val expected = renjuRule.checkOverline(blackStones, newStone)

        // then
        assertThat(expected).isEqualTo(true)
    }

    @Test
    fun `If 5 black stones are in a row, it is win even if it is double four`() {
        // given
        val blackStones = listOf(
            Pair(5, 5), Pair(5, 6),
            Pair(5, 8), Pair(5, 9),
            Pair(6, 6), Pair(6, 8),
            Pair(4, 6), Pair(4, 8),
            Pair(3, 5), Pair(3, 9),
        )
        val whiteStones = listOf<Pair<Int, Int>>()
        val newStone = Pair(5, 7)

        // when
        val expected = renjuRule.checkWin(blackStones, whiteStones, newStone, 5)

        // then
        assertThat(expected).isTrue
    }

    @Test
    fun `If 5 black stones are in a row, it is win even if it is double three`() {
        // given
        val blackStones = listOf(
            Pair(5, 5), Pair(5, 7),
            Pair(5, 8), Pair(5, 9),
            Pair(4, 5), Pair(4, 6),
            Pair(6, 6), Pair(6, 7),
        )
        val whiteStones = emptyList<Pair<Int, Int>>()
        val newStone = Pair(5, 6)

        // when
        val expected = renjuRule.checkWin(blackStones, whiteStones, newStone, 5)

        // then
        assertThat(expected).isTrue
    }
}
