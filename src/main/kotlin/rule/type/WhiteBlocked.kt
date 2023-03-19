package rule.type

enum class WhiteBlocked(val state: Boolean) {
    BLOCKED(true),
    NON_BLOCK(false);

    companion object {
        const val INNER_DISTANCE: Int = 6

        fun of(isBlocked: Boolean): WhiteBlocked =
            WhiteBlocked.values().first { it.state == isBlocked }
    }
}
