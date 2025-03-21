package rule.type

internal enum class Violation(val state: Boolean) {
    DOUBLE_THREE(true),
    DOUBLE_FOUR(true),
    OVERLINE(true),
    NONE(false);

    companion object {
        const val OVERLINE_SIZE = 6
        const val FOUL_CONDITION_SIZE = 2
        const val MAX_EMPTY_SIZE = 1
    }
}
