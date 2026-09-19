package no.victoria.hitsterapp.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.PlayArea
import no.victoria.hitsterapp.model.SpotifyPlaylist

class PlayerRepository(
    private val db: FirebaseFirestore
) {
    fun createPlayer(): String {
        val playerRef = db.collection("players").document()
        val playerId = playerRef.id

        val playerData = hashMapOf(
            "playerId" to playerId,
            "createdAt" to System.currentTimeMillis(),
            "nickname" to "",
            "playArea" to hashMapOf(
                "tempTimeline" to hashMapOf<String, Any>(),
                "permanentTimeline" to hashMapOf<String, Any>()
            ),
            "hitsterCards" to 3
        )

        playerRef.set(playerData)

        return playerId
    }

    fun updateNickname(playerId: String, nickname: String) {
        val playerRef = db.collection("players").document(playerId)

        playerRef.update("nickname", nickname)
    }

    suspend fun getNickname(playerId: String): String {
        val document = db.collection("players").document(playerId).get().await()

        return document.getString("nickname") ?: ""
    }

    suspend fun updatePlayArea(playerId: String, playArea: PlayArea) {
        val playerRef = db.collection("players").document(playerId)

        val tempTimeline = playArea.tempTimeline.map { card ->
            hashMapOf(
                "title" to card.title,
                "artist" to card.artist,
                "releaseYear" to card.releaseYear,
                "songId" to card.songId
            )
        }

        val permanentTimeline = playArea.timeline.map { card ->
            hashMapOf(
                "title" to card.title,
                "artist" to card.artist,
                "releaseYear" to card.releaseYear,
                "songId" to card.songId
            )
        }

        playerRef.set(
            mapOf("playArea" to mapOf(
                "tempTimeline" to tempTimeline,
                "permanentTimeline" to permanentTimeline
            )),
            SetOptions.merge()
        ).await()
    }

    suspend fun getPlayArea(playerId: String): PlayArea {
        val document = db.collection("players").document(playerId).get().await()

        val tempTimelineData = document.get("playArea.tempTimeline") as? List<Map<String, Any>> ?: emptyList()

        val tempTimeline = tempTimelineData.map { data ->
            MusicCard(
                title = data["title"] as String,
                artist = data["artist"] as String,
                releaseYear = data["releaseYear"] as Int,
                songId = data["songId"] as String
            )
        }

        val permanentTimelineData = document.get("playArea.permanentTimeline") as? List<Map<String, Any>> ?: emptyList()

        val permanentTimeline = permanentTimelineData.map { data ->
            MusicCard(
                title = data["title"] as String,
                artist = data["artist"] as String,
                releaseYear = data["releaseYear"] as Int,
                songId = data["songId"] as String
            )
        }

        return PlayArea(permanentTimeline, tempTimeline)
    }

    suspend fun removeHitsterCard(playerId: String) {
        val playerRef = db.collection("players").document(playerId)

        val document = playerRef.get().await()
        val currentCards = document.getLong("hitsterCards") ?: 0

        if (currentCards <= 0) return

        Log.d("vikkan", "$currentCards there are htistacard")

        playerRef.update("hitsterCards", currentCards - 1).await()
    }

    suspend fun getHitsterCards(playerId: String): Int {
        val document = db.collection("players").document(playerId).get().await()

        return (document.getLong("hitsterCards") ?: 0).toInt()
    }
}
