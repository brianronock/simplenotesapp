package com.example.simplenotesapp.screen

// Import statements for Android OS components (Handler, Looper) and
// Jetpack Compose UI elements, layout modifiers, state management, and navigation.
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Composable function for the Splash Screen.
 * This screen is displayed for a short duration when the app starts,
 * and then automatically navigates to the main content (NoteListScreen).
 *
 * @param navController The NavController used for navigating to the next screen.
 */
@Composable
fun SplashScreen(navController: NavController) {
    // LaunchedEffect is a coroutine builder that runs side effects (like navigation after a delay)
    // when the composable enters the composition.
    // The key `true` means this effect runs once when SplashScreen is first composed.
    LaunchedEffect(true) {
        // Use Android's Handler to post a delayed action on the main UI thread.
        // Looper.getMainLooper() ensures the action runs on the main thread,
        // which is necessary for UI operations like navigation.
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to the "note_list" screen.
            navController.navigate("note_list") {
                // Configure the navigation action:
                // popUpTo("splash") removes the "splash" screen from the back stack.
                // inclusive = true means the "splash" screen itself is also popped.
                // This prevents the user from navigating back to the splash screen
                // after it has served its purpose.
                popUpTo("splash") { inclusive = true }
            }
        }, 2000) // Delay in milliseconds (2000 ms = 2 seconds).
    }

    // Box is a layout composable that positions its children relative to its edges.
    // It's often used for simple layouts or for overlaying children.
    Box(
        // Modifier.fillMaxSize() makes the Box take up the entire available screen space.
        modifier = Modifier.fillMaxSize(),
        // contentAlignment = Alignment.Center centers the children within the Box.
        contentAlignment = Alignment.Center
    ) {
        // Text composable to display the "NOTES" title.
        Text("NOTES", fontSize = 32.sp) // Sets the font size to 32 scale-independent pixels.
    }
}
