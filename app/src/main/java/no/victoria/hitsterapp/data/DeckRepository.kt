package no.victoria.hitsterapp.data

import android.util.Log
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.SpotifyPlaylist
import no.victoria.hitsterapp.model.dto.MusicCardDto
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class DeckRepository(
) {
    suspend fun fetchPlaylistItemsJson(
        playlistId: String,
        accessToken: String
    ): String? = withContext(Dispatchers.IO) {
        val fields = "items(item(album(release_date), name, artists(name), uri))"

        val url =
            "https://api.spotify.com/v1/playlists/$playlistId/items?fields=$fields"

        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d("DeckRepository", "Code: ${response.code}")
            Log.d("DeckRepository", "Body: $responseBody")

            responseBody
        } catch (e: Exception) {
            Log.e("DeckRepository", "Request failed", e)
            null
        }
    }

    fun parseMusicCards(responseBody: String): List<MusicCardDto> {
        val root = JSONObject(responseBody)
        val items = root.getJSONArray("items")

        val cards = mutableListOf<MusicCardDto>()

        for (i in 0 until items.length()) {
            try {
                val itemObject = items.getJSONObject(i)
                val track = itemObject.getJSONObject("item")

                val title = track.getString("name")

                val artistsArray = track.getJSONArray("artists")
                val artists = mutableListOf<String>()
                for (j in 0 until artistsArray.length()) {
                    artists += artistsArray.getJSONObject(j).getString("name")
                }

                val artist = artists.joinToString(", ")

                val album = track.getJSONObject("album")
                val releaseYear = album.getString("release_date").take(4).toInt()
                val songId = track.getString("uri")

                cards += MusicCardDto(
                    title = title,
                    artist = artist,
                    releaseYear = releaseYear,
                    songId = songId
                )
            } catch (e: Exception) {
                Log.e("DeckRepository", "Error parsing JSON", e)
            }
        }
        return cards
    }

    suspend fun createCardsFromPlaylist(
        playlistId: String,
        accessToken: String
    ): List<MusicCard> {
        val responseBody = fetchPlaylistItemsJson(playlistId, accessToken)
        return if (responseBody != null) {
            parseMusicCards(responseBody).map { musicCardDto ->
                MusicCard(
                    musicCardDto.title,
                    musicCardDto.artist,
                    musicCardDto.releaseYear,
                    musicCardDto.songId
                )
            }
        } else {
            emptyList()
        }
    }

    suspend fun createPlaylist(
        playlistName: String,
        description: String = "Hitster-playlist",
        public: Boolean = true,
        accessToken: String
    ): String = withContext(Dispatchers.IO) {
        val url = "https://api.spotify.com/v1/me/playlists"

        try {
            val client = OkHttpClient()

            val jsonMediaType = "application/json".toMediaType();

            val jsonBody = """
            {
                "name": "$playlistName",
                "description": "$description",
                "public": $public
            }
            """.trimIndent()

            val body = jsonBody.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("DeckRepository", "Code: ${response.code}")
            Log.d("DeckRepository", "Body: $responseBody")

            val jsonResponseBody = JSONObject(responseBody)
            val playlistId = jsonResponseBody.getString("id")

            Log.d("DeckRepo", "Playlist created")

            playlistId

        } catch (e: Exception) {
            Log.e("DeckRepository", "createplaylist request failed", e)
            ""
        }
    }

    suspend fun addSongs(
        playlistId: String,
        accessToken: String,
    ): String? = withContext(Dispatchers.IO) {
        val url = "https://api.spotify.com/v1/playlists/$playlistId/items"

        val hitsterUris = listOf(
            "spotify:track:0raZgJSrAyL70ke6rfsqK7", "spotify:track:6SOmLQXDnvZhOHoQQue50V",
            "spotify:track:36EWx5B78n8sSfpmtKrUUV", "spotify:track:2Qm5DrmotzWvqNM3JlcQYo",
            "spotify:track:1ue7zm5TVVvmoQV8lK6K2H", "spotify:track:41Fflg7qHiVOD6dEPvsCzO",
            "spotify:track:0mEcvxv1Wmx0jN65IJVZSK", "spotify:track:6FRtjadDsEhZIQIlh1loEM",
            "spotify:track:5fRvePkRGdpn2nKacG7I6d", "spotify:track:4mhDjLuXWYYb8dDUy08u04",
            "spotify:track:1Q1b8eVkUPGlpSArl8JAVw", "spotify:track:4GPe1qjQCvOIa8vLBKDI9d",
            "spotify:track:0HVfSExFkEcEvTi88T0ceC", "spotify:track:3Mmt6Xk0H6VR92PEp6x3hP",
            "spotify:track:4ACxa9buEUnOdYEoPcnMpi", "spotify:track:6NgYIETQ8U72CVfkzYhK30",
            "spotify:track:21qnJAMtzC6S5SESuqQLEK", "spotify:track:5hkab0WgO1XksXkDVltk33",
            "spotify:track:2qT1uLXPVPzGgFOx4jtEuo", "spotify:track:7utoClKnLShFg6u6dZ20gp",
            "spotify:track:09IStsImFySgyp0pIQdqAc", "spotify:track:1eOJAiCKFuMda0fPRvjcuc",
            "spotify:track:5b7OgznPJJr1vHNYGyvxau", "spotify:track:1fidCEsYlaVE3pHwKCvpFZ",
            "spotify:track:7CWmnyipXqy7WFb1VrULfM", "spotify:track:65i1UPsUtPlEVzewEZR6sY",
            "spotify:track:0qJ0QrdO2a5PWgxnGgaMmY", "spotify:track:1CtAzw53AIXKjAemxy4b1j",
            "spotify:track:2T3jgoAMbBXiMMD8pSc6QL", "spotify:track:5t4fNLNmmIxw576bnkfkJ6",
            "spotify:track:0Gf6mZ2kMJCDVHm2afcpfl", "spotify:track:1UzofFX5AkfTDnwjcBkM4J",
            "spotify:track:7bkjJHsfX2rebJ3KZLegGt", "spotify:track:3MrWxJaD2AT0W9DjWF64Vm",
            "spotify:track:7rPzEczIS574IgPaiPieS3", "spotify:track:5NwkWwfRJaT55hEPtCmJHx",
            "spotify:track:3Nf8oGn1okobzjDcFCvT6n", "spotify:track:6qUEOWqOzu1rLPUPQ1ECpx",
            "spotify:track:2BP4JYtL9Crj3EGrSqd37k", "spotify:track:3rmo8F54jFF8OgYsqTxm5d",
            "spotify:track:5vYA1mW9g2Coh1HUFUSmlb", "spotify:track:3FQihovssDSPosdZ4O1DxU",
            "spotify:track:6ZR2HOk1tTRtLwBWmHviP4", "spotify:track:4DX82Vc8qAH4jJPvKxvwg6",
            "spotify:track:7dwJqwTXDtsrX1QLqb0o96", "spotify:track:17WZ2MrQQtUncVcKkyU8Eo",
            "spotify:track:26kvblHxXkWe3j4bwyOEfG", "spotify:track:7pbEKdMWHGwPQSDGfcNycM",
            "spotify:track:3QRM0qZB7oMYavveH0iEqx", "spotify:track:1hRFVIy9As8OVRk8B7CrD5",
            "spotify:track:77Nw4E8ji9K2vG23rQikBc", "spotify:track:6UelLqGlWMcVH1E5c4H7lY",
            "spotify:track:5Hroj5K7vLpIG4FNCRIjbP", "spotify:track:18DfMhEx4ddoreHrvZDF6Q",
            "spotify:track:63xdwScd1Ai1GigAwQxE8y", "spotify:track:1i1OuCNhNf7JwrlWlFZFu1",
            "spotify:track:0DaOFUhUEc416QdUaW9paE", "spotify:track:2WZ3zMVFxuC5i20jQrnPyE",
            "spotify:track:4hrae8atte6cRlSC9a7VCO", "spotify:track:0fYVliAYKHuPmECRs1pbRf",
            "spotify:track:5KqldkCunQ2rWxruMEtGh0", "spotify:track:1jcPcDu2YawPfLhwjYnqK2",
            "spotify:track:4ok9JLaKi5Er6bIZ9VRgkh", "spotify:track:0GONea6G2XdnHWjNZd6zt3",
            "spotify:track:2JzZzZUQj3Qff7wapcbKjc", "spotify:track:7DFNE7NO0raLIUbgzY2rzm",
            "spotify:track:3Yi21KBstONWGrdjuWszlt", "spotify:track:12nCsFJb47CXg86xVaKf8k",
            "spotify:track:64gdv7HDzLmVBUlBf8sGYZ", "spotify:track:5tewIdMVsaJWN19ZnmnPNN",
            "spotify:track:4qhLwbEBUGUN0eNlzXAQON", "spotify:track:70LrxJ5u19umvrXbC19g20",
            "spotify:track:3ZI3sK9ZJB3LOiZtCs9l3p", "spotify:track:0catVIxBH7B5F6S5obaKkd",
            "spotify:track:5XcZRgJv3zMhTqCyESjQrF", "spotify:track:78TVWUTtCC4WAhi0SRPxQK",
            "spotify:track:5qrSlOut2rNAWv3ubArkNy", "spotify:track:28UMiBhn383n9S7GL4tsxD",
            "spotify:track:5yGTQzYbEdY6B9RFZJypgt", "spotify:track:1Jr9IcDZqYYVMmE853Yc5u",
            "spotify:track:07q0QVgO56EorrSGHC48y3", "spotify:track:1lkvpmrCaXK8QtliFDcHBO",
            "spotify:track:5L8ta4ECl5zeA6bGqY7G38", "spotify:track:4RADreHMvMkZwsPgPr9z5c",
            "spotify:track:0e7ipj03S05BNilyu5bRzt", "spotify:track:3ydfhgIZIc2j39NLIhpJpq",
            "spotify:track:1hEh8Hc9lBAFWUghHBsCel", "spotify:track:46ydq5g3k17iLJs3qMDvO6",
            "spotify:track:3ZPhnsk7LPDPVbhyUP7VoT", "spotify:track:5YB2ap2JDJmg5c2GcQkaxH",
            "spotify:track:7I7fLUK6IDGTxU5QRl7I8v", "spotify:track:7kyMLaXepcGHIrsLqoiTB8",
            "spotify:track:5lhD4efbXGJTqabqwB1Klh", "spotify:track:3z8h0TU7ReDPLIbEnYhWZb",
            "spotify:track:4Hd5t36BUvthvHUW0uLleb", "spotify:track:0b9djfiuDIMw1zKH6gV74g",
            "spotify:track:2TGlAwAHzYXbdFHCWdpI23", "spotify:track:6i727ACer8XMtKMT4ZGmc1",
            "spotify:track:7qtAgn9mwxygsPOsUDVRRt", "spotify:track:1k1Bqnv2R0uJXQN4u6LKYt"
        )

        try {
            val client = OkHttpClient()

            val jsonMediaType = "application/json".toMediaType()

            val urisJson = hitsterUris.joinToString(",") { "\"$it\"" }

            val jsonBody = """
            {
                "uris": [$urisJson]
            }
            """.trimIndent()

            val body = jsonBody.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            responseBody
        } catch (e: Exception) {
            Log.e("DeckRepository", "addTopSongs request failed", e)
            null
        }
    }

    suspend fun getAllSpotifyPlaylists(
        accessToken: String,
    ): List<SpotifyPlaylist>? = withContext(Dispatchers.IO) {
        val url = "https://api.spotify.com/v1/me/playlists"

        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $accessToken")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            val jsonResponseBody = JSONObject(responseBody)
            val items = jsonResponseBody.getJSONArray("items")

            val playlists = mutableListOf<SpotifyPlaylist>()


            for (i in 0 until items.length()) {
                val playlist = items.getJSONObject(i)
                val id = playlist.getString("id")
                val name = playlist.getString("name")
                val images = playlist.optJSONArray("images")
                val imageUrl = if (images != null && images.length() > 0) {
                    images.getJSONObject(0).optString("url", null)
                } else {
                    null
                }
                playlists.add(SpotifyPlaylist(
                    id = id,
                    name = name,
                    imageUrl = imageUrl
                )
                )
            }
            playlists

        } catch (e: Exception) {
            Log.e("DeckRepository", "getallspotplaylists request failed", e)
            null
        }
    }
}
