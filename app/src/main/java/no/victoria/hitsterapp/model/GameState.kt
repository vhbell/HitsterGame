package no.victoria.hitsterapp.model

data class GameState(
    val id: String,
    val players: List<String>,
    val state: States = States.LOBBY,
)

enum class States {
    LOBBY, PLAYING, FINISHED, HOME
}