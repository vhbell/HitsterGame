package no.victoria.hitsterapp.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import no.victoria.hitsterapp.game.GameManager
import no.victoria.hitsterapp.model.SpotifyPlaylist
import no.victoria.hitsterapp.ui.screens.LobbyScreen

@Composable
fun LobbyRoute(
    code: String,
    isHost: Boolean,
    playlists: List<SpotifyPlaylist>,
    gameManager: GameManager,
    onPlaylistClick: (String) -> Unit,
    onStartGame: (String) -> Unit,
    onLeaveLobby: () -> Unit,
    onStatusChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var playerNicknames by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(code) {
        playerNicknames = gameManager.getPlayerNicknames(code)

        gameManager.listenToPlayerChange(code, onPlayerChange = { playerIds ->
            scope.launch {
                playerNicknames = gameManager.getPlayerNicknames(playerIds)
            }
        })
        gameManager.listenToGameStatus(code, onStatusChanged = {status ->
            onStatusChanged(status)
        })
    }

    LobbyScreen(
        gameCode = code,
        players = playerNicknames,
        isHost = isHost,
        playlists = playlists,
        onPlaylistClick =  onPlaylistClick,
        onStartGame = { onStartGame(code) },
        onLeaveLobby = onLeaveLobby,
        modifier = modifier
    )
}