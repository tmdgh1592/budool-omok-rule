package rule

enum class KoRule(val state: Boolean) {
    KO_ALL(true),
    KO_THREE_TO_THREE(true),
    KO_FOUR_TO_FOUR(true),
    KO_OVERLINE(true),
    NOT_KO(false),
}
