package no.victoria.hitsterapp.rules

import android.util.Log
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.PlayArea

class GamblingRuleSet: RuleSet {

    override fun playerTurn() {

    }

    override fun isCorrectPlacement(card: MusicCard, playArea: PlayArea, index: Int): Boolean {
        val timeline = playArea.tempTimeline

        val leftCard = timeline.getOrNull(index - 1)
        val rightCard = timeline.getOrNull(index)
        Log.d("isCorrectPlacement", "leftCard: $leftCard, rightCard: $rightCard")


        val isAfterLeft = leftCard == null || leftCard.releaseYear <= card.releaseYear
        val isBeforeRight = rightCard == null || card.releaseYear <= rightCard.releaseYear

        Log.d("isCorrectPlacement", "isAfterLeft: $isAfterLeft, isBeforeRight: $isBeforeRight")

        return isAfterLeft && isBeforeRight
    }

    override fun endTurn(playArea: PlayArea): PlayArea {
        return playArea.copy(
            timeline = playArea.tempTimeline,
            tempTimeline = playArea.tempTimeline
        )
    }
}