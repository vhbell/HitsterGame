package no.victoria.hitsterapp.model

import no.victoria.hitsterapp.config.GameMode

data class GameUiState(
    val deck: Deck? = null,
    val currentCard: MusicCard? = null,
    val playArea: PlayArea = PlayArea(),
    val placedGapIndex: Int? = null,
    val turn: Boolean = false,
    val gameMode: GameMode,
    val canEndTurn: Boolean = false,
    val hitsterCards: Int = 0,
)
