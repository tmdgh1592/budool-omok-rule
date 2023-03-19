package rule

enum class WhiteBlockedStatus(val state: Boolean) {
    WhiteBLOCKED(true),
    NON_BLOCK(false);

    companion object {
        const val INNER_DISTANCE: Int = 6

        fun of(isBlocked: Boolean): WhiteBlockedStatus =
            WhiteBlockedStatus.values().first { it.state == isBlocked }
    }
}
