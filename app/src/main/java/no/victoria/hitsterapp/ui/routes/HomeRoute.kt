package no.victoria.hitsterapp.ui.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import no.victoria.hitsterapp.ui.screens.HomeScreen

@Composable
fun HomeRoute(
    onCreateLobby: (String) -> Unit,
    onJoinLobby: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    HomeScreen(
        onCreateLobby = onCreateLobby,
        onJoinLobby = onJoinLobby,
        modifier = modifier
    )
}
