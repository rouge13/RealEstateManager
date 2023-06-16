package com.openclassrooms.realestatemanager.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.ui.MainApplication
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InitializationViewModel : ViewModel() {
    fun startInitialization(application: MainApplication) {
        viewModelScope.launch {
            application.waitForInitialization()
        }
    }
}