package no.victoria.hitsterapp.game

import SpotifyManager
import android.util.Log
import no.victoria.hitsterapp.data.DeckRepository
import no.victoria.hitsterapp.data.firebase.GameRepository
import no.victoria.hitsterapp.data.firebase.PlayerRepository
import no.victoria.hitsterapp.model.Deck
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.PlayArea
import no.victoria.hitsterapp.model.SpotifyPlaylist

class GameManager(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val deckRepository: DeckRepository,
) {
    suspend fun createLobby(playerId: String, nickname: String): String {
        playerRepository.updateNickname(playerId, nickname)

        val gameCode = gameRepository.createGame(playerId)

        return gameCode
    }

    suspend fun getPlaylists(gameCode: String, accessToken: String): List<SpotifyPlaylist> {
        val spotifyPlaylists = deckRepository.getAllSpotifyPlaylists(accessToken)
        if (spotifyPlaylists != null) {
            gameRepository.savePlaylists(gameCode, spotifyPlaylists)
            return spotifyPlaylists
        }
        return emptyList()
    }

    fun createPlayer(): String {
        return playerRepository.createPlayer()
    }

    suspend fun joinLobby(gameCode: String, playerId: String, nickname: String) {
        playerRepository.updateNickname(playerId, nickname)

        gameRepository.joinGame(gameCode, playerId)
    }

    suspend fun startGame(gameCode: String, accessToken: String) {
        gameRepository.startGame(gameCode)

        val playlistId = gameRepository.getPlaylistId(gameCode)
        if (playlistId != null) {
            this.populateDeckFromPlaylist(gameCode, playlistId, accessToken)
        }
    }

    suspend fun populateDeckFromPlaylist(gameCode: String, playlistId: String, accessToken: String) {
        val cards = deckRepository.createCardsFromPlaylist(playlistId, accessToken)
        gameRepository.uploadDeckInGame(gameCode, Deck(cards.toMutableList()))
    }

    suspend fun getPlayerNicknames(gameCode: String): List<String> {
        val playerIds = gameRepository.getPlayers(gameCode)
        return playerIds.map { playerId ->
            playerRepository.getNickname(playerId)
        }
    }

    suspend fun getDeck(gameCode: String): Deck {
        return gameRepository.getDeck(gameCode)
    }

    suspend fun drawCard(gameCode: String): MusicCard? {
        val deck = gameRepository.getDeck(gameCode)

        if (deck.musicCards.isEmpty()) {
            return null
        }

        val drawnCard = deck.musicCards.removeAt(0)

        gameRepository.uploadDeckInGame(gameCode, deck)
        gameRepository.updateCurrentCard(gameCode, drawnCard)

        Log.d("GameManager", "Drawn card: $drawnCard")

        return drawnCard
    }

    suspend fun updatePlayArea(playerId: String, playArea: PlayArea) {
        playerRepository.updatePlayArea(playerId, playArea)
    }

    suspend fun getPlayArea(playerId: String): PlayArea {
        return playerRepository.getPlayArea(playerId)
    }

    suspend fun getPlayerTurn(gameId: String): String {
        val playerId = gameRepository.getPlayerTurn(gameId)
        return playerRepository.getNickname(playerId)
    }

    suspend fun listenToGameStatus(gameCode: String, onStatusChanged: (String) -> Unit) {
        gameRepository.listenToGameStatus(gameCode, onStatusChanged)
    }

    suspend fun getPlayerNicknames (playerIds: List<String>): List<String> {
        return playerIds.map { id ->
            playerRepository.getNickname(id)
        }
    }

    suspend fun listenToPlayerChange(gameCode: String, onPlayerChange: (List<String>) -> Unit) {
        gameRepository.listenToPlayers(gameCode, onPlayerChange)
    }

    suspend fun listenToCurrentCardChange(gameCode: String, onCurrentCardChange: (String) -> Unit) {
        gameRepository.listenToCurrentCardChange(gameCode, onCurrentCardChange)
    }

    suspend fun addTurnListner(gameCode: String, onPlayerTurnChange: (String) -> Unit) {
        gameRepository.addTurnListner(gameCode, onPlayerTurnChange)
    }

    suspend fun endTurn(gameCode: String) {
        gameRepository.nextPlayer(gameCode)
    }

    suspend fun getPlayerNickname(playerId: String): String {
        return playerRepository.getNickname(playerId)
    }

    suspend fun clearCurrentCard(gameCode: String) {
        return gameRepository.clearCurrentCard(gameCode)
    }

    suspend fun useHitsterCard(playerId: String) {
        playerRepository.removeHitsterCard(playerId)
    }

    suspend fun getHitsterCards(playerId: String): Int {
        return playerRepository.getHitsterCards(playerId)
    }

    suspend fun createPlaylist(accessToken: String): String {
        val playlistId = deckRepository.createPlaylist("MyFirstPlaylist", "NoDesc", true, accessToken)
        deckRepository.addSongs(playlistId, accessToken)
        return playlistId
    }

    suspend fun choosePlaylist(gameCode: String, playlistId: String) {
        gameRepository.choosePlaylist(gameCode, playlistId)
    }
}

