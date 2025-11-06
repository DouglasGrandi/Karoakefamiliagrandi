package net.natura.karoakefamiliagrandi.domain.util

object YoutubeIdExtractor {
    private val patterns = listOf(
        "v=([a-zA-Z0-9_-]{11})",
        "youtu.be/([a-zA-Z0-9_-]{11})",
        "shorts/([a-zA-Z0-9_-]{11})"
    ).map { Regex(it) }

    fun extractId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        if (trimmed.matches(Regex("[a-zA-Z0-9_-]{11}"))) return trimmed
        for (rx in patterns) {
            rx.find(trimmed)?.let { return it.groupValues[1] }
        }
        return null
    }
}
