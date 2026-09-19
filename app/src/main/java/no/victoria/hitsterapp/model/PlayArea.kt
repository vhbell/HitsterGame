package no.victoria.hitsterapp.model

data class PlayArea(
    val timeline: List<MusicCard> = emptyList(),
    val tempTimeline: List<MusicCard> = emptyList()
)
