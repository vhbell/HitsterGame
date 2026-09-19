package no.victoria.hitsterapp

import SpotifyManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import no.victoria.hitsterapp.data.DeckRepository
import no.victoria.hitsterapp.data.firebase.GameRepository
import no.victoria.hitsterapp.data.firebase.PlayerRepository
import no.victoria.hitsterapp.game.GameManager
import no.victoria.hitsterapp.model.States
import no.victoria.hitsterapp.rules.GamblingRuleSet
import no.victoria.hitsterapp.ui.routes.HomeRoute
import no.victoria.hitsterapp.ui.routes.LobbyRoute
import no.victoria.hitsterapp.ui.routes.PlayingRoute
import no.victoria.hitsterapp.ui.theme.HitsterAppTheme
import no.victoria.hitsterapp.viewmodel.AppViewModel
import no.victoria.hitsterapp.viewmodel.AppViewModelFactory
import no.victoria.hitsterapp.viewmodel.GameViewModel
import no.victoria.hitsterapp.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var spotifyManager: SpotifyManager
    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(GamblingRuleSet())
    }

    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory(gameManager)
    }
    private val db by lazy { Firebase.firestore }
    private val playerRepository by lazy { PlayerRepository(db) }
    private val gameRepository by lazy { GameRepository(db) }
    private val deckRepository by lazy { DeckRepository() }

    private val gameManager by lazy {
        GameManager(
            gameRepository = gameRepository,
            playerRepository = playerRepository,
            deckRepository = deckRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // spotifyManager created for all players, but only initialized for host
        spotifyManager = SpotifyManager(this)

        setContent {
            HitsterAppTheme {
                val scope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (appViewModel.currentScreen) {
                        States.HOME -> {
                            HomeRoute(
                                onCreateLobby = { nickname ->
                                    appViewModel.createLobby(nickname) { createdGameCode ->
                                        spotifyManager.authorize { accessToken ->
                                            appViewModel.loadPlaylists(createdGameCode, accessToken)
                                        }

                                        scope.launch {
                                            gameManager.listenToCurrentCardChange(createdGameCode) { songId ->
                                                spotifyManager.playSong(songId)
                                            }
                                        }
                                    }
                                },
                                onJoinLobby = { code, nickname ->
                                    appViewModel.joinLobby(code, nickname)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        States.LOBBY -> {
                            val code = appViewModel.gameCode ?: return@Scaffold

                            LobbyRoute(
                                code = code,
                                isHost = appViewModel.isHost,
                                playlists = appViewModel.playlists,
                                gameManager = gameManager,
                                onPlaylistClick = { playlist ->
                                    scope.launch {
                                        gameManager.choosePlaylist(code, playlist)
                                    }
                                },
                                onStartGame = { startCode ->
                                    val accessToken = spotifyManager.getAccessToken()
                                    appViewModel.startGame(startCode, accessToken)
                                },
                                onLeaveLobby = appViewModel::goHome,
                                onStatusChanged = appViewModel::setScreenFromStatus,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        States.PLAYING -> {
                            val code = appViewModel.gameCode ?: return@Scaffold

                            PlayingRoute(
                                code = code,
                                appViewModel = appViewModel,
                                gameViewModel = gameViewModel,
                                gameManager = gameManager,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        else -> {
                            appViewModel.goHome()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
       super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}
