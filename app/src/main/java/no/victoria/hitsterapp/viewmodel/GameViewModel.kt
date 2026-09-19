package no.victoria.hitsterapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.victoria.hitsterapp.config.GameMode
import no.victoria.hitsterapp.model.GameUiState
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.PlayArea
import no.victoria.hitsterapp.rules.RuleSet

class GameViewModel(
    private val ruleSet: RuleSet,
) : ViewModel() {

    var uiState by mutableStateOf(
        GameUiState(
            gameMode = GameMode.GAMBLING)
    )

    fun clearPlacedGap() {
        uiState = uiState.copy(placedGapIndex = null)
    }

    fun drawCard() {
        val cards = uiState.deck?.musicCards
        if (cards != null && cards.isNotEmpty()) {
            val drawnCard = cards.removeAt(0)
            uiState = uiState.copy(currentCard = drawnCard, canEndTurn = false)
        } else {
            uiState = uiState.copy(currentCard = null)
        }
    }

    fun skipCard() {
        drawCard()
    }

    fun placeCardInPlayArea(index: Int) {
        uiState = uiState.copy(placedGapIndex = index)
    }

    fun flipCard(): Boolean {
        val card = uiState.currentCard ?: return false
        val index = uiState.placedGapIndex ?: return false
        val playArea = uiState.playArea
        val tempTimeline = playArea.tempTimeline.toMutableList()
        val isCorrectPlacement = ruleSet.isCorrectPlacement(card, playArea, index)

        if (isCorrectPlacement) {
            tempTimeline.add(index, card)
            uiState = uiState.copy(
                playArea = playArea.copy(tempTimeline = tempTimeline),
                currentCard = null,
                placedGapIndex = null,
                canEndTurn = true
            )
        } else {
            val permanentTimeline = playArea.timeline

            uiState = uiState.copy(currentCard = null, placedGapIndex = null, playArea = PlayArea(tempTimeline = permanentTimeline))
            this.endTurn()
        }
        return isCorrectPlacement
    }

    fun endTurn() {
        val updatedPlayArea = ruleSet.endTurn(uiState.playArea)

        uiState = uiState.copy(
            playArea = updatedPlayArea
        )
    }

    fun setCurrentCard(card: MusicCard?) {
        uiState = uiState.copy(currentCard = card)
    }

    fun onHitster() {
        if (uiState.turn) {
            this.skipCard()
        }
    }
}
