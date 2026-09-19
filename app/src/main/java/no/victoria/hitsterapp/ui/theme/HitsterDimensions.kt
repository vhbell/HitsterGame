package no.victoria.hitsterapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

data class HitsterDimensions(
    val cardSize: Dp,
    val cardPadding: Dp,
    val cardCornerSize: Dp,
    val cardBorderWidth: Dp,
    val cardTitleTextSize: TextUnit,
    val cardTitleLineHeight: TextUnit,
    val cardYearTextSize: TextUnit,
    val cardArtistTextSize: TextUnit,
    val cardArtistLineHeight: TextUnit,
    val cardBackTextSize: TextUnit,
    val cardBackCenterTextSize: TextUnit,
    val snapZoneCollapsedWidth: Dp,
    val snapZoneExpandedWidth: Dp,
    val screenPadding: Dp,
    val contentSpacing: Dp,
    val deckExtraWidth: Dp,
    val deckExtraHeight: Dp,
    val deckBackOffsetLargeX: Dp,
    val deckBackOffsetLargeY: Dp,
    val deckBackOffsetSmallX: Dp,
    val deckBackOffsetSmallY: Dp,
    val hitsterCardSize: Dp,
    val hitsterInfoButtonSize: Dp,
    val hitsterCardTextSize: TextUnit
)

@Composable
fun rememberHitsterDimensions(): HitsterDimensions {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val cardSize = when {
        screenWidth < 360.dp -> 96.dp
        screenWidth < 420.dp -> 110.dp
        else -> 120.dp
    }

    val titleTextSize = (cardSize.value * 0.12f).sp
    val artistTextSize = (cardSize.value * 0.09f).sp

    return HitsterDimensions(
        cardSize = cardSize,
        cardPadding = cardSize * 0.08f,
        cardCornerSize = cardSize * 0.12f,
        cardBorderWidth = 2.dp,
        cardTitleTextSize = titleTextSize,
        cardTitleLineHeight = (titleTextSize.value * 1.15f).sp,
        cardYearTextSize = (cardSize.value * 0.24f).sp,
        cardArtistTextSize = artistTextSize,
        cardArtistLineHeight = (artistTextSize.value * 1.15f).sp,
        cardBackTextSize = titleTextSize,
        cardBackCenterTextSize = (cardSize.value * 0.34f).sp,
        snapZoneCollapsedWidth = cardSize * 0.2f,
        snapZoneExpandedWidth = cardSize + 2*cardSize * 0.2f,
        screenPadding = 16.dp,
        contentSpacing = 16.dp,
        deckExtraWidth = cardSize * 0.2f,
        deckExtraHeight = cardSize * 0.17f,
        deckBackOffsetLargeX = cardSize * -0.13f,
        deckBackOffsetLargeY = cardSize * 0.08f,
        deckBackOffsetSmallX = cardSize * -0.07f,
        deckBackOffsetSmallY = cardSize * 0.04f,
        hitsterCardSize = cardSize * 0.68f,
        hitsterInfoButtonSize = cardSize * 0.27f,
        hitsterCardTextSize = (cardSize.value * 0.09f).sp
    )
}
