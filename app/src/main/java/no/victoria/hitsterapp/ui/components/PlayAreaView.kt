package no.victoria.hitsterapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.times
import no.victoria.hitsterapp.model.PlayArea
import no.victoria.hitsterapp.ui.theme.HitsterDimensions
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions

@Composable
fun PlayAreaView(
    playArea: PlayArea,
    activeGapIndex: Int?,
    placedGapIndex: Int?,
    dimensions: HitsterDimensions = rememberHitsterDimensions(),
    onSnapZoneMeasured: (SnapZone) -> Unit,
    gapContent: @Composable (Int) -> Unit = {},
) {
    val timeline = playArea.tempTimeline

    LazyRow {
        timeline.forEachIndexed { index, card ->
            item {
                SnapZoneSlot(
                    index = index,
                    isActive = activeGapIndex == index,
                    dimensions = dimensions,
                    onSnapZoneMeasured = onSnapZoneMeasured,
                    containsCard = placedGapIndex == index,
                ) {
                    gapContent(index)
                }
            }

            item {
                MusicCardView(
                    musicCard = card,
                    showFront = true,
                    dimensions = dimensions,
                    onBoundsChanged = {}
                )
            }
        }

        item {
            SnapZoneSlot(
                index = timeline.size,
                isActive = activeGapIndex == timeline.size,
                dimensions = dimensions,
                onSnapZoneMeasured = onSnapZoneMeasured,
                containsCard = placedGapIndex == timeline.size,
            )
            {
                gapContent(timeline.size)
            }
        }
    }
}

@Composable
fun SnapZoneSlot(
    index: Int,
    isActive: Boolean,
    containsCard: Boolean,
    dimensions: HitsterDimensions = rememberHitsterDimensions(),
    onSnapZoneMeasured: (SnapZone) -> Unit,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(
                when {
                    containsCard -> dimensions.cardSize + 2*dimensions.snapZoneCollapsedWidth

                    isActive -> dimensions.snapZoneExpandedWidth

                    else -> dimensions.snapZoneCollapsedWidth
                }
            )
            .height(dimensions.cardSize)
            .onGloballyPositioned { coordinates ->
                onSnapZoneMeasured(
                    SnapZone(
                        index = index,
                        bounds = coordinates.boundsInWindow(),
                        snapOffset = coordinates.positionInWindow()
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
