package no.victoria.hitsterapp.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import no.victoria.hitsterapp.game.GameManager
import no.victoria.hitsterapp.ui.screens.MainScreen
import no.victoria.hitsterapp.viewmodel.AppViewModel
import no.victoria.hitsterapp.viewmodel.GameViewModel

@Composable
fun PlayingRoute(
    code: String,
    appViewModel: AppViewModel,
    gameViewModel: GameViewModel,
    gameManager: GameManager,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val playerId = appViewModel.playerId

    LaunchedEffect(code) {
        val deck = gameManager.getDeck(code)
        val playArea = gameManager.getPlayArea(playerId)
        val turnName = gameManager.getPlayerTurn(code)
        val hitsterCards = gameManager.getHitsterCards(playerId)

        appViewModel.updatePlayerTurn(turnName)

        gameViewModel.uiState = gameViewModel.uiState.copy(
            deck = deck,
            playArea = playArea,
            hitsterCards = hitsterCards
        )

        gameManager.addTurnListner(code) { turnPlayerId ->
            scope.launch {
                gameViewModel.uiState = gameViewModel.uiState.copy(
                    turn = turnPlayerId == playerId
                )

                appViewModel.updatePlayerTurn(
                    gameManager.getPlayerNickname(turnPlayerId)
                )
            }
        }
    }

    MainScreen(
        state = gameViewModel.uiState,
        playerTurn = appViewModel.playerTurn,
        onDrawCard = {
            scope.launch {
                val drawnCard = gameManager.drawCard(code)
                gameViewModel.drawCard()
                gameViewModel.setCurrentCard(drawnCard)
            }
        },
        onPlaceCard = { index ->
            gameViewModel.placeCardInPlayArea(index)

            scope.launch {
                gameManager.clearCurrentCard(code)
            }
        },
        onFlipCard = {
            scope.launch {
                if (gameViewModel.flipCard()) {
                    gameManager.updatePlayArea(
                        playerId,
                        gameViewModel.uiState.playArea
                    )
                } else {
                    gameViewModel.endTurn()
                    gameManager.updatePlayArea(
                        playerId,
                        gameViewModel.uiState.playArea
                    )
                    gameManager.endTurn(code)
                }
            }
        },
        onClearPlacedGap = gameViewModel::clearPlacedGap,
        onEndTurn = {
            scope.launch {
                gameViewModel.endTurn()
                gameManager.updatePlayArea(
                    playerId,
                    gameViewModel.uiState.playArea
                )
                gameManager.endTurn(code)
            }
        },
        onHitster = {
            scope.launch {
                gameManager.useHitsterCard(playerId)
                gameViewModel.uiState = gameViewModel.uiState.copy(
                    hitsterCards = (gameViewModel.uiState.hitsterCards - 1).coerceAtLeast(0)
                )
                gameViewModel.onHitster()
                gameManager.drawCard(code)
            }
        },
        modifier = modifier
    )
}
