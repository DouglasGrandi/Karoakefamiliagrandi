package net.natura.karoakefamiliagrandi.domain.model

data class Score(
    val songId: String,
    val total: Int,
    val energy: Int,
    val stability: Int,
)
