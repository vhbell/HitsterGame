package no.victoria.hitsterapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import no.victoria.hitsterapp.model.Deck
import no.victoria.hitsterapp.ui.theme.HitsterDimensions
import no.victoria.hitsterapp.ui.theme.HitsterDeepPurple
import no.victoria.hitsterapp.ui.theme.HitsterPurple
import no.victoria.hitsterapp.ui.theme.HitsterSurfaceBright
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions

@Composable
fun DeckView(
    deck: Deck,
    dimensions: HitsterDimensions = rememberHitsterDimensions()
) {
    val cards = deck.musicCards

    Box(
        modifier = Modifier.size(
            width = dimensions.cardSize + dimensions.deckExtraWidth,
            height = dimensions.cardSize + dimensions.deckExtraHeight
        ),
        contentAlignment = Alignment.Center
    ) {
        DeckBackCard(
            modifier = Modifier.offset(
                x = dimensions.deckBackOffsetLargeX,
                y = dimensions.deckBackOffsetLargeY
            ),
            dimensions = dimensions
        )

        DeckBackCard(
            modifier = Modifier.offset(
                x = dimensions.deckBackOffsetSmallX,
                y = dimensions.deckBackOffsetSmallY
            ),
            dimensions = dimensions
        )

        if (cards.isNotEmpty()) {
            MusicCardView(
                musicCard = cards[0],
                modifier = Modifier,
                showFront = false,
                dimensions = dimensions
            )
        }
    }
}

@Composable
private fun DeckBackCard(
    modifier: Modifier = Modifier,
    dimensions: HitsterDimensions = rememberHitsterDimensions()
) {
    Box(
        modifier = modifier
            .size(dimensions.cardSize)
            .clip(RoundedCornerShape(dimensions.cardCornerSize))
            .background(
                Brush.verticalGradient(
                    listOf(
                        HitsterSurfaceBright,
                        HitsterPurple
                    )
                )
            )
            .border(
                width = dimensions.cardBorderWidth,
                color = HitsterDeepPurple,
                shape = RoundedCornerShape(dimensions.cardCornerSize)
            )
    )
}
