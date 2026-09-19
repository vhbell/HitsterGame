import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

class SpotifyManager(
    private val activity: ComponentActivity,
) {
    private val clientId = "2ffa101742b34f398c24ac0cab070b40"
    private val redirectUri = "no.victoria.hitsterapp://callback"
    private var accessToken: String? = null

    var spotifyAppRemote: SpotifyAppRemote? = null
        private set
    private var onAuthorized: ((String) -> Unit)? = null

    private val authLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val response = AuthorizationClient.getResponse(result.resultCode, result.data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val token = response.accessToken
                    if (token != null) {
                        accessToken = token
                        onAuthorized?.invoke(token)
                        connectAppRemote()
                    } else {
                        Log.e("Spotify", "Auth success but token is null")
                    }
                }

                AuthorizationResponse.Type.ERROR -> {
                    Log.e("Spotify", "Auth error: ${response.error}")
                }

                else -> {
                    Log.e("Spotify", "Auth cancelled or empty response: ${response.type}")
                }
            }
        }

    fun authorize(onAuthorized: (String) -> Unit = {}) {
        this.onAuthorized = onAuthorized

        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(
                arrayOf(
                    "app-remote-control",
                    "playlist-read-private",
                    "playlist-read-collaborative",
                    "playlist-modify-public",
                    "playlist-modify-private"
                )
            )
            .build()

        val intent = AuthorizationClient.createLoginActivityIntent(activity, request)
        authLauncher.launch(intent)
    }

    fun disconnect() {
        spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }
        spotifyAppRemote = null
    }

    private fun connectAppRemote() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(false)
            .build()

        SpotifyAppRemote.connect(
            activity,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("Spotify", "Connected!")
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("Spotify", "Connect failed", throwable)
                }
            }
        )
    }

    fun playSong(songId: String) {
        spotifyAppRemote?.playerApi?.play(songId)
    }

    fun getAccessToken(): String {
        return accessToken ?: throw IllegalStateException("Not authorized")
    }
}