package no.victoria.hitsterapp.rules

import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.PlayArea

interface RuleSet {
    fun playerTurn()

    fun isCorrectPlacement(card: MusicCard, playArea: PlayArea, index: Int): Boolean
    fun endTurn(playArea: PlayArea): PlayArea
}