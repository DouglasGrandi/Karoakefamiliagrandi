package net.natura.karoakefamiliagrandi.domain.model

data class Song(
    val id: String,
    val title: String,
    val channel: String? = null,
    val durationSec: Int? = null
)
