package no.victoria.hitsterapp.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.ui.theme.HitsterDimensions
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions
import kotlin.math.roundToInt

@Composable
fun DraggableMusicCardView(
    musicCard: MusicCard,
    modifier: Modifier = Modifier,
    dimensions: HitsterDimensions = rememberHitsterDimensions(),
    onDragStarted: () -> Unit,
    onDragPositionChanged: (Rect?) -> Unit,
    onDragEnded: (Rect?) -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var cardBounds by remember { mutableStateOf<Rect?>(null) }
    var showFront = false

    Box {
        MusicCardView(
            musicCard = musicCard,
            modifier = modifier.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .onGloballyPositioned { coordinates ->
                    cardBounds = coordinates.boundsInWindow()
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        onClick()
                        showFront = true
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            onDragStarted()
                        },
                        onDragEnd = {
                            onDragEnded(cardBounds)
                        },
                        onDragCancel = {
                            onDragPositionChanged(null)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        onDragPositionChanged(cardBounds)
                    }
                },
            showFront = showFront,
            dimensions = dimensions
        )
    }
}
