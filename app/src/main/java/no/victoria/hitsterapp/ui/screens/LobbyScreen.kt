package no.victoria.hitsterapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import no.victoria.hitsterapp.model.SpotifyPlaylist
import no.victoria.hitsterapp.ui.theme.rememberHitsterDimensions
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun LobbyScreen(
    gameCode: String,
    players: List<String>,
    playlists: List<SpotifyPlaylist>,
    isHost: Boolean,
    onStartGame: () -> Unit,
    onLeaveLobby: () -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = rememberHitsterDimensions()

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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LobbyHeader(gameCode)
                    LobbyActions(
                        isHost = isHost,
                        onStartGame = onStartGame,
                        onLeaveLobby = onLeaveLobby
                    )
                    PlayLists(
                        spotifyPlaylists = playlists,
                        onPlaylistClick = {playlist -> onPlaylistClick(playlist.id)}
                    )
                }
                PlayersList(
                    players = players,
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 440.dp)
                )
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LobbyHeader(gameCode)
                    PlayersList(players = players)
                    PlayLists(
                        spotifyPlaylists = playlists,
                        onPlaylistClick = {playlist -> onPlaylistClick(playlist.id)}
                    )
                }
                LobbyActions(
                    isHost = isHost,
                    onStartGame = onStartGame,
                    onLeaveLobby = onLeaveLobby
                )
            }
        }
    }
}

@Composable
private fun LobbyHeader(gameCode: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Lobby",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Game code",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = gameCode,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PlayersList(
    players: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Players (${players.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            players.forEachIndexed { index, player ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp,
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (index == 0) {
                            Text(
                                text = "Host",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun PlayLists(
    spotifyPlaylists: List<SpotifyPlaylist>,
    onPlaylistClick: (SpotifyPlaylist) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(spotifyPlaylists) { playlist ->
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onPlaylistClick(playlist) },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = playlist.imageUrl,
                    contentDescription = playlist.name,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LobbyActions(
    isHost: Boolean,
    onStartGame: () -> Unit,
    onLeaveLobby: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isHost) {
            Button(
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start game")
            }
        }

        OutlinedButton(
            onClick = onLeaveLobby,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Leave lobby")
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
