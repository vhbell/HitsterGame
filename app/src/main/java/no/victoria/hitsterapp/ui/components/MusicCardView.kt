package no.victoria.hitsterapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.ui.theme.HitsterDimensions
import no.victoria.hitsterapp.ui.theme.HitsterDeepPurple
import no.victoria.hitsterapp.ui.theme.HitsterMagenta
import no.victoria.hitsterapp.ui.theme.HitsterPink
import no.victoria.hitsterapp.ui.theme.HitsterPurple
import no.victoria.hitsterapp.ui.theme.HitsterTeal
import no.victoria.hitsterapp.ui.theme.HitsterText
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions

@Composable
fun MusicCardView(
    musicCard: MusicCard,
    modifier: Modifier = Modifier,
    showFront: Boolean = false,
    dimensions: HitsterDimensions = rememberHitsterDimensions(),
    onBoundsChanged: (Rect?) -> Unit = {}
) {
    if (showFront) {
        FrontCard(musicCard, modifier, dimensions, onBoundsChanged)
    } else {
        BackCard(modifier, dimensions)
    }
}

@Composable
fun FrontCard(
    musicCard: MusicCard,
    modifier: Modifier = Modifier,
    dimensions: HitsterDimensions = rememberHitsterDimensions(),
    onBoundsChanged: (Rect?) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .requiredSize(dimensions.cardSize)
            .clip(RoundedCornerShape(dimensions.cardCornerSize))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF7FF),
                        Color(0xFFEFD7FF)
                    )
                )
            )
            .border(
                width = dimensions.cardBorderWidth,
                color = HitsterDeepPurple,
                shape = RoundedCornerShape(dimensions.cardCornerSize)
            )
            .padding(dimensions.cardPadding)
            .onGloballyPositioned { coordinates ->
                val cardBounds = coordinates.boundsInWindow()
                onBoundsChanged(cardBounds)
            }
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = musicCard.title,
            fontSize = dimensions.cardTitleTextSize,
            fontWeight = FontWeight.ExtraBold,
            color = HitsterDeepPurple,
            lineHeight = dimensions.cardTitleLineHeight
        )

        Text(
            text = musicCard.releaseYear.toString(),
            fontSize = dimensions.cardYearTextSize,
            fontWeight = FontWeight.Black,
            color = HitsterMagenta,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = musicCard.artist,
            fontSize = dimensions.cardArtistTextSize,
            fontWeight = FontWeight.Medium,
            color = HitsterPurple,
            lineHeight = dimensions.cardArtistLineHeight
        )
    }
}

@Composable
fun BackCard(
    modifier: Modifier = Modifier,
    dimensions: HitsterDimensions = rememberHitsterDimensions()
) {
    Box(
        modifier = modifier
            .requiredSize(dimensions.cardSize)
            .clip(RoundedCornerShape(dimensions.cardCornerSize))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        HitsterTeal,
                        HitsterPink,
                        HitsterPurple
                    )
                )
            )
            .border(
                width = dimensions.cardBorderWidth,
                color = HitsterDeepPurple,
                shape = RoundedCornerShape(dimensions.cardCornerSize)
            )
            .padding(dimensions.cardPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HITSTER",
                fontSize = dimensions.cardBackTextSize,
                fontWeight = FontWeight.Black,
                color = HitsterText
            )

            Text(
                text = "H",
                fontSize = dimensions.cardBackCenterTextSize,
                fontWeight = FontWeight.Black,
                color = HitsterText
            )

            Text(
                text = "HITSTER",
                fontSize = dimensions.cardBackTextSize,
                fontWeight = FontWeight.Black,
                color = HitsterText
            )
        }
    }
}
