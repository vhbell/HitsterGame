package no.victoria.hitsterapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.victoria.hitsterapp.rules.RuleSet

class GameViewModelFactory(
    private val ruleSet: RuleSet
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(ruleSet) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}