package no.victoria.hitsterapp.config

data class GameConfig(
    val gameMode: GameMode = GameMode.GAMBLING,
)

enum class GameMode {
    GAMBLING
}