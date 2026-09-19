package no.victoria.hitsterapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions

@Composable
fun HomeScreen(
    onCreateLobby: (String) -> Unit,
    onJoinLobby: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = rememberHitsterDimensions()
    var joinCode by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

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
    ) {
        val isWide = maxWidth >= 640.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensions.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HomeHeader(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeForm(
                        nickname = nickname,
                        onNicknameChange = { nickname = it },
                        joinCode = joinCode,
                        onJoinCodeChange = { joinCode = it.uppercase() },
                        onJoinLobby = { onJoinLobby(joinCode.trim(), nickname) }
                    )
                    Button(
                        onClick = { onCreateLobby(nickname) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create lobby")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(dimensions.screenPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    HomeHeader()
                    HomeForm(
                        nickname = nickname,
                        onNicknameChange = { nickname = it },
                        joinCode = joinCode,
                        onJoinCodeChange = { joinCode = it.uppercase() },
                        onJoinLobby = { onJoinLobby(joinCode.trim(), nickname) }
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onCreateLobby(nickname) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create lobby")
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "HITSTER",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Build a lobby, play the track, place the year.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HomeForm(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    joinCode: String,
    onJoinCodeChange: (String) -> Unit,
    onJoinLobby: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nickname") },
                singleLine = true
            )

            OutlinedTextField(
                value = joinCode,
                onValueChange = onJoinCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Game code") },
                singleLine = true
            )

            Button(
                onClick = onJoinLobby,
                modifier = Modifier.fillMaxWidth(),
                enabled = joinCode.isNotBlank()
            ) {
                Text("Join lobby")
            }
        }
    }
}
