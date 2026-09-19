package no.victoria.hitsterapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.victoria.hitsterapp.game.GameManager

class AppViewModelFactory(
    private val gameManager: GameManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(gameManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}