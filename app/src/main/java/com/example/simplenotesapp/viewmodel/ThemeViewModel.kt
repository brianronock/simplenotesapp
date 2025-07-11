package com.example.simplenotesapp.viewmodel

// Import statements for Jetpack Compose runtime (though not directly used in this ViewModel's logic,
// it's often included if you plan to use Compose-specific utilities within ViewModels,
// or if other parts of the module use it extensively) and AndroidX Lifecycle components.
// ViewModel is a class designed to store and manage UI-related data in a lifecycle-conscious way.
// MutableStateFlow and StateFlow are part of Kotlin Coroutines, used for creating observable data streams.
// import androidx.compose.runtime.* // Example: Not strictly needed here, but often present.
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.asStateFlow // Could be used if _isDarkTheme was not directly assigned

/**
 * ViewModel responsible for managing the application's theme state (dark or light mode).
 * It holds the current theme preference and provides a way to toggle it.
 * UI components can observe the theme state from this ViewModel to reactively update their appearance.
 */
class ThemeViewModel : ViewModel() {

    // _isDarkTheme is a private MutableStateFlow that holds the current theme preference.
    // MutableStateFlow is a state-holder observable flow that emits the current and new state updates to its collectors.
    // It's initialized to `false`, meaning the default theme is light.
    // The underscore prefix (`_`) is a common convention for internal, mutable state holders.
    private val _isDarkTheme = MutableStateFlow(false)

    // isDarkTheme is a public, immutable StateFlow that exposes the current theme preference to the UI.
    // UI components (like Activities or Composables) can collect from this StateFlow to observe theme changes.
    // It directly exposes the private _isDarkTheme as a read-only StateFlow.
    // This encapsulation prevents external classes from directly modifying the theme state,
    // enforcing that changes only happen through the `toggleTheme` method.
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme // Could also be _isDarkTheme.asStateFlow() for stricter read-only exposure

    /**
     * Toggles the current theme preference.
     * If the current theme is dark, it changes to light, and vice versa.
     * This method updates the internal `_isDarkTheme` StateFlow, which in turn
     * notifies any observers of `isDarkTheme`.
     *
     * In a more complete application, this function might also trigger saving
     * the new theme preference to persistent storage (e.g., DataStore or SharedPreferences)
     * so that the choice is remembered across app sessions.
     */
    fun toggleTheme() {
        // Invert the current boolean value of _isDarkTheme.
        // Accessing '.value' on a MutableStateFlow gets or sets its current value.
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Optional: If you were persisting the theme, you might have an init block
    // to load the saved preference when the ViewModel is created:
    // init {
    //     viewModelScope.launch {
    //         // Example: Assuming dataStore.readThemePreference() returns a Flow<Boolean?>
    //         val savedPreference = dataStore.readThemePreference().firstOrNull() ?: false // Default to light if nothing saved
    //         _isDarkTheme.value = savedPreference
    //     }
    // }
}

