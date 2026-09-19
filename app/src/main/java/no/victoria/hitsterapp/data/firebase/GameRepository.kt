package no.victoria.hitsterapp.data.firebase

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import no.victoria.hitsterapp.model.Deck
import no.victoria.hitsterapp.model.MusicCard
import no.victoria.hitsterapp.model.SpotifyPlaylist
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

class GameRepository(
    private val db: FirebaseFirestore
) {
    suspend fun createGame(playerId: String): String {
        val gameRef = db.collection("games").document()
        val gameCode = generateGameCode()
        val gameId = gameRef.id

        val gameData = hashMapOf(
            "gameId" to gameId,
            "gameCode" to gameCode,
            "createdAt" to System.currentTimeMillis(),
            "status" to "LOBBY",
            "host" to playerId,
            "players" to listOf(playerId)
        )

        gameRef.set(gameData).await()
        return gameCode
    }

    private suspend fun generateGameCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        while (true) {
            val code = (1..4)
                .map { chars.random() }
                .joinToString("")

            val document = db.collection("games").document(code).get().await()

            if (!document.exists()) {
                return code
            }
        }
    }

    suspend fun joinGame(gameCode: String, playerId: String) {

        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef = db.collection("games").document(gameId)

        gameRef.update("players", FieldValue.arrayUnion(playerId))
            .addOnSuccessListener {
                println("Player joined game with code: $gameCode")
            }
            .addOnFailureListener { e ->
                println("Error joining game: $e")
            }
    }

    suspend fun startGame(gameCode: String) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef = db.collection("games").document(gameId)

        val document = gameRef.get().await()

        val playerIds = document.get("players") as? List<String> ?: emptyList()

        if (playerIds.isEmpty()) {
            println("No players in game")
            return
        }

        val randomStartingPerson = playerIds.random()
        gameRef.update(
            mapOf(
                "status" to "PLAYING",
                "turn" to randomStartingPerson)
            ).await()
    }

    suspend fun getPlayers(gameCode: String): List<String> {
        val gameId = getGameIdFromGameCode(gameCode) ?: return emptyList()

        val document = db.collection("games")
            .document(gameId)
            .get()
            .await()

        return document.get("players") as? List<String> ?: emptyList()
    }

    suspend fun uploadDeckInGame(gameCode: String, deck: Deck) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef = db.collection("games").document(gameId)

        val deckData = deck.musicCards.map { card ->
            hashMapOf(
                "title" to card.title,
                "artist" to card.artist,
                "releaseYear" to card.releaseYear,
                "songId" to card.songId
            )
        }

        gameRef.set(
            mapOf("deck" to deckData),
            SetOptions.merge()
        ).await()
    }

    suspend fun getDeck(gameCode: String): Deck {
        val gameId = getGameIdFromGameCode(gameCode) ?: return Deck(emptyList<MusicCard>() as MutableList<MusicCard>)

        val document = db.collection("games")
            .document(gameId)
            .get()
            .await()

        val deckData = document.get("deck") as? List<Map<String, Any>> ?: emptyList()

        val cards = deckData.map { data ->
            MusicCard(
                title = data["title"] as String,
                artist = data["artist"] as String,
                releaseYear = (data["releaseYear"] as Long).toInt(),
                songId = data["songId"] as String
            )
        }

        return Deck(cards.toMutableList())
    }

    suspend fun updateCurrentCard(gameCode: String, card: MusicCard) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef = db.collection("games").document(gameId)
        val cardData = hashMapOf(
            "title" to card.title,
            "artist" to card.artist,
            "releaseYear" to card.releaseYear,
            "songId" to card.songId
        )
        gameRef.set(
            mapOf("currentCard" to cardData),
            SetOptions.merge()
        ).await()
    }

    suspend fun getPlayerTurn(gameCode: String): String {
        val gameId = getGameIdFromGameCode(gameCode) ?: return "No player"

        val gameRef = db.collection("games").document(gameId)
        val document = gameRef.get().await()

        return document.get("turn") as String
    }

    private suspend fun getGameIdFromGameCode(gameCode: String): String? {
        val query = db.collection("games")
            .whereEqualTo("gameCode", gameCode.uppercase())
            .limit(1).
            get().
            await()

        val document = query.documents.firstOrNull()

        val gameId = document?.id

        return gameId
    }

    suspend fun getHost(gameCode: String): String {
        val gameId = getGameIdFromGameCode(gameCode) ?: return "No host"

        val document = db.collection("games").document(gameId).get().await()

        return document.get("host") as String
    }

    suspend fun listenToGameStatus(gameCode: String, onStatusChanged: (String) -> Unit) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        db.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error listening to game status: $error")
                    return@addSnapshotListener
                }

                val status = snapshot?.getString("status") ?: return@addSnapshotListener

                onStatusChanged(status)
            }
    }

    suspend fun listenToPlayers(gameCode: String, onPlayerChange: (List<String>) -> Unit) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        db.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error listening to game status: $error")
                    return@addSnapshotListener
                }

                val players = snapshot?.get("players") as? List<String> ?: return@addSnapshotListener

                onPlayerChange(players)
            }
    }

    suspend fun listenToCurrentCardChange(gameCode: String, onCurrentCardChange: (String) -> Unit) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        db.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error listening to game status: $error")
                    return@addSnapshotListener
                }

                val currentSongId = snapshot?.getString("currentCard.songId") ?: return@addSnapshotListener

                onCurrentCardChange(currentSongId)
            }
    }

    suspend fun addTurnListner(gameCode: String, onPlayerTurnChange: (String) -> Unit) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        db.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error listening to game status: $error")
                    return@addSnapshotListener
                }

                val playerTurn = snapshot?.getString("turn") ?: return@addSnapshotListener

                onPlayerTurnChange(playerTurn)
            }
    }

    suspend fun nextPlayer(gameCode: String) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef = db.collection("games").document(gameId)

        val players = this.getPlayers(gameCode)
        if (players.isEmpty()) return

        val currentPlayer = this.getPlayerTurn(gameCode)
        val currentIndex = players.indexOf(currentPlayer)

        val nextIndex = if (currentIndex == -1) {
            0
        } else {
            (currentIndex + 1) % players.size
        }

        val newPlayerTurn = players[nextIndex]

        gameRef.update("turn", newPlayerTurn).await()
    }

    suspend fun clearCurrentCard(gameCode: String) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef =  db.collection("games").document(gameId)

        gameRef.update("currentCard", null).await()
    }


    suspend fun savePlaylists(gameCode: String, spotifyPlaylists: List<SpotifyPlaylist>) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return

        val gameRef = db.collection("games").document(gameId)

        val playlists = spotifyPlaylists.map { playlist ->
            hashMapOf(
                "id" to playlist.id,
                "name" to playlist.name,
                "imageUrl" to playlist.imageUrl
            )
        }

        gameRef.set(
            mapOf("playlists" to playlists),
            SetOptions.merge()
        ).await()
    }

    suspend fun choosePlaylist(gameCode: String, playlistId: String) {
        val gameId = getGameIdFromGameCode(gameCode) ?: return
        val gameRef = db.collection("games").document(gameId)

        gameRef.set(
            mapOf("playlistId" to playlistId),
            SetOptions.merge()
        ).await()
    }

    suspend fun getPlaylistId(gameCode: String): String? {
        val gameId = getGameIdFromGameCode(gameCode) ?: return null
        val gameRef = db.collection("games").document(gameId)

        val document = gameRef.get().await()

        return document.get("playlistId") as String
    }
}
