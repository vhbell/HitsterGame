package no.victoria.hitsterapp.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

data class SnapZone(
    val index: Int,
    val bounds: Rect,
    val snapOffset: Offset
)
