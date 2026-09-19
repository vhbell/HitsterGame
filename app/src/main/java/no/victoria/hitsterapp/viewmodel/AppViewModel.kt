package no.victoria.hitsterapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.rpc.Code
import kotlinx.coroutines.launch
import no.victoria.hitsterapp.game.GameManager
import no.victoria.hitsterapp.model.SpotifyPlaylist
import no.victoria.hitsterapp.model.States

class AppViewModel(
    private val gameManager: GameManager
): ViewModel() {
    var playerId by mutableStateOf(gameManager.createPlayer())
        private set

    var currentScreen by mutableStateOf(States.HOME)
        private set

    var gameCode by mutableStateOf<String?>(null)
        private set

    var playerTurn by mutableStateOf("No player")
        private set

    var isHost by mutableStateOf(false)
        private set

    var playlists by mutableStateOf<List<SpotifyPlaylist>>(emptyList())
        private set

    // return to home screen
    fun goHome() {
        gameCode = null
        isHost = false
        currentScreen = States.HOME
    }

    // go to lobby
    fun goLobby(code: String, host: Boolean) {
        gameCode = code
        isHost = host
        currentScreen = States.LOBBY
    }

    fun loadPlaylists(gameCode: String, accessToken: String) {
        viewModelScope.launch {
            playlists = gameManager.getPlaylists(gameCode, accessToken)
        }
    }

    fun selectPlaylist(gameCode: String, playlistId: String) {
        viewModelScope.launch {
            gameManager.choosePlaylist(gameCode, playlistId)
        }
    }

    fun createLobby(nickname: String, onHostCreated: (String) -> Unit) {
        viewModelScope.launch {
            val createdGameCode = gameManager.createLobby(playerId, nickname)
            goLobby(createdGameCode, true)

            onHostCreated(createdGameCode)
        }
    }

    fun joinLobby(code: String, nickname: String) {
        viewModelScope.launch {
            gameManager.joinLobby(code, playerId, nickname)
            goLobby(code, false)
        }
    }

    fun startGame(code: String, accessToken: String) {
        if (!isHost) return

        viewModelScope.launch {
            gameManager.startGame(code, accessToken)
            currentScreen = States.PLAYING
        }
    }

    fun setScreenFromStatus(status: String) {
        currentScreen = States.valueOf(status)
    }

    fun updatePlayerTurn(name: String) {
        playerTurn = name
    }

    suspend fun getPlaylists(gameCode: String, accessToken: String): List<SpotifyPlaylist> {
        return gameManager.getPlaylists(gameCode, accessToken)
    }
}
