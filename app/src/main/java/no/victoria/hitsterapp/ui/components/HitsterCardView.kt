package no.victoria.hitsterapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.victoria.hitsterapp.ui.theme.HitsterDeepPurple
import no.victoria.hitsterapp.ui.theme.HitsterPink
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions

@Composable
fun HitsterCardView(
    onHitster: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfo by remember { mutableStateOf(false) }
    val dimensions = rememberHitsterDimensions()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onHitster,
                shape = CircleShape,
                modifier = Modifier.size(dimensions.hitsterCardSize),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HitsterPink,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "HITSTER",
                    fontSize = dimensions.hitsterCardTextSize,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    softWrap = false
                )
            }

            FilledTonalIconButton(
                onClick = { showInfo = true },
                modifier = Modifier.size(dimensions.hitsterInfoButtonSize)
            ) {
                Text("?", color = HitsterDeepPurple)
            }
        }
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = {
                Text("Hitster card")
            },
            text = {
                Text("Use the Hitster card when the rules allow it. Depending on the moment, it can be used to steal or skip.")
            },
            confirmButton = {
                TextButton(
                    onClick = { showInfo = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
