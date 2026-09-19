package no.victoria.hitsterapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.victoria.hitsterapp.config.GameMode
import no.victoria.hitsterapp.model.GameUiState
import no.victoria.hitsterapp.ui.components.DeckView
import no.victoria.hitsterapp.ui.components.DraggableMusicCardView
import no.victoria.hitsterapp.ui.components.HitsterCardView
import no.victoria.hitsterapp.ui.components.PlayAreaView
import no.victoria.hitsterapp.ui.components.SnapZone
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions

@Composable
fun MainScreen(
    state: GameUiState,
    playerTurn: String,
    onDrawCard: () -> Unit,
    onPlaceCard: (Int) -> Unit,
    onFlipCard: () -> Unit,
    onClearPlacedGap: () -> Unit,
    onEndTurn: () -> Unit,
    onHitster: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deck = state.deck
    val currentCard = state.currentCard
    val playArea = state.playArea
    val placedGapIndex = state.placedGapIndex
    val gameMode = state.gameMode
    val canEndTurn = state.canEndTurn
    val turn = state.turn
    val hitsterCards = state.hitsterCards

    val dimensions = rememberHitsterDimensions()
    var activeGapIndex by remember { mutableStateOf<Int?>(null) }
    var snapZones by remember { mutableStateOf(listOf<SnapZone>()) }
    var draggingFromGapIndex by remember { mutableStateOf<Int?>(null) }
    var endTurnError by remember { mutableStateOf<String?>(null) }

    val renderGapIndex = placedGapIndex ?: draggingFromGapIndex

    fun findSnapZone(cardBounds: Rect?): SnapZone? {
        return cardBounds?.let { card ->
            snapZones.maxByOrNull { zone ->
                val overlapLeft = maxOf(card.left, zone.bounds.left)
                val overlapRight = minOf(card.right, zone.bounds.right)
                val overlapTop = maxOf(card.top, zone.bounds.top)
                val overlapBottom = minOf(card.bottom, zone.bounds.bottom)

                val overlapWidth = maxOf(0f, overlapRight - overlapLeft)
                val overlapHeight = maxOf(0f, overlapBottom - overlapTop)

                overlapWidth * overlapHeight
            }
        }
    }

    fun onDragPositionChanged(rect: Rect?) {
        activeGapIndex = findSnapZone(rect)?.index
    }

    fun onDragStarted() {
        if (placedGapIndex != null) {
            draggingFromGapIndex = placedGapIndex
            onClearPlacedGap()
        }
    }

    fun onDragEnded(rect: Rect?) {
        val zone = findSnapZone(rect)

        if (zone != null) {
            onPlaceCard(zone.index)
        }

        draggingFromGapIndex = null
        activeGapIndex = null
    }

    @Composable
    fun gapContent(index: Int) {
        if (currentCard != null && index == renderGapIndex) {
            DraggableMusicCardView(
                musicCard = currentCard,
                dimensions = dimensions,
                onDragStarted = ::onDragStarted,
                onDragPositionChanged = ::onDragPositionChanged,
                onDragEnded = ::onDragEnded,
                onClick = onFlipCard,
            )
        }
    }

    @Composable
    fun TurnHeader(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "$playerTurn's turn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    fun DeckControls() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensions.contentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (deck != null) {
                DeckView(deck, dimensions = dimensions)
                if (turn) {
                    Button(onClick = onDrawCard) {
                        Text("Draw card")
                    }
                }
            } else {
                Text(text = "Loading deck...")
            }
        }
    }

    @Composable
    fun CurrentCardControls() {
        when {
            currentCard == null -> {
                Text(
                    text = "No card drawn",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            placedGapIndex == null && draggingFromGapIndex == null -> {
                DraggableMusicCardView(
                    musicCard = currentCard,
                    dimensions = dimensions,
                    onDragStarted = ::onDragStarted,
                    onDragPositionChanged = ::onDragPositionChanged,
                    onDragEnded = ::onDragEnded,
                    onClick = onFlipCard
                )
            }
        }
    }

    @Composable
    fun TurnControls() {
        if (gameMode == GameMode.GAMBLING) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (canEndTurn) {
                            endTurnError = null
                            onEndTurn()
                        } else {
                            endTurnError = "Place the card before ending your turn"
                        }
                    }
                ) {
                    Text("End turn")
                }

                endTurnError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    @Composable
    fun HitsterControls() {
        if (hitsterCards > 0) {
            HitsterCardView(
                onHitster = onHitster
            )
        }
    }

    @Composable
    fun BottomControls(
        isWide: Boolean,
        modifier: Modifier = Modifier
    ) {
        if (isWide) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(dimensions.contentSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DeckControls()
                CurrentCardControls()
                TurnControls()
                HitsterControls()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeckControls()
                CurrentCardControls()
                TurnControls()
                HitsterControls()
            }
        }
    }

    @Composable
    fun TimelinePanel(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.58f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayAreaView(
                playArea = playArea,
                activeGapIndex = activeGapIndex,
                dimensions = dimensions,
                onSnapZoneMeasured = { zone ->
                    snapZones = if (snapZones.any { it.index == zone.index }) {
                        snapZones.map {
                            if (it.index == zone.index) zone else it
                        }
                    } else {
                        snapZones + zone
                    }
                },
                gapContent = { gapContent(it) },
                placedGapIndex = placedGapIndex,
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(dimensions.screenPadding)
    ) {
        val isWide = maxWidth >= 720.dp

        if (isWide) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TurnHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TimelinePanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .zIndex(0f)
                )
                BottomControls(
                    isWide = true,
                    modifier = Modifier.zIndex(1f)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TurnHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TimelinePanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .zIndex(0f)
                )
                BottomControls(
                    isWide = false,
                    modifier = Modifier.zIndex(1f)
                )
            }
        }
    }
}
