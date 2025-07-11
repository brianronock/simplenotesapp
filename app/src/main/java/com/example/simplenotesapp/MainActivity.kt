package com.example.simplenotesapp

// Import statements for core Android Activity components, Jetpack Compose UI,
// Navigation component, ViewModel utilities, and application-specific classes.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.simplenotesapp.screen.AddNoteScreen
import com.example.simplenotesapp.screen.NoteListScreen
import com.example.simplenotesapp.ui.theme.SimpleNotesAppTheme // Custom theme for the app.
import com.example.simplenotesapp.viewmodel.NoteViewModel // ViewModel for note-related logic.
import com.example.simplenotesapp.screen.SplashScreen // Splash screen composable.
import com.example.simplenotesapp.viewmodel.ThemeViewModel // ViewModel for theme management.
// import androidx.compose.material3.MaterialTheme // Not directly used here, SimpleNotesAppTheme wraps it.
import androidx.compose.ui.platform.LocalContext // Provides access to the current Context.
import com.example.simplenotesapp.data.NoteDatabase // Room database class.
import com.example.simplenotesapp.viewmodel.NoteViewModelFactory // Factory for creating NoteViewModel.

/**
 * The main entry point of the application.
 * This Activity hosts the Jetpack Compose UI and sets up the navigation graph.
 */
class MainActivity : ComponentActivity() { // Inherits from ComponentActivity, the base class for activities using Jetpack Compose.

    /**
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views, bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Always call the superclass's implementation.

        // setContent is the entry point for building the UI with Jetpack Compose.
        // The lambda provided to setContent defines the root composable of the activity.
        setContent {
            // Obtain an instance of ThemeViewModel using the viewModel() delegate.
            // This ViewModel will manage the theme state (dark/light).
            val themeViewModel: ThemeViewModel = viewModel()

            // Create and remember a NavController.
            // This controller is responsible for managing app navigation within the NavHost.
            // rememberNavController() ensures the controller persists across recompositions.
            val navController: NavHostController = rememberNavController()

            // Get the current application context.
            // LocalContext.current provides the Context Ambient, which is the current Context.
            val context = LocalContext.current

            // Get an instance of the Room database.
            // NoteDatabase.getDatabase(context) provides a singleton instance of the database.
            val db = NoteDatabase.getDatabase(context)

            // Get an instance of the NoteDao from the database.
            // The DAO provides methods to interact with the 'notes' table.
            val noteDao = db.noteDao()

            // Create a factory for the NoteViewModel.
            // This is necessary because NoteViewModel has a constructor parameter (noteDao).
            val factory = NoteViewModelFactory(noteDao)

            // Obtain an instance of NoteViewModel using the viewModel() delegate and the custom factory.
            // This ViewModel will handle the business logic related to notes.
            val noteViewModel: NoteViewModel = viewModel(factory = factory)

            // Observe the 'isDarkTheme' StateFlow from the ThemeViewModel.
            // 'collectAsState()' converts the Flow into a Compose State object.
            // The 'by' delegate unwraps this State, and ensures that when 'isDarkTheme'
            // in the ViewModel changes, this part of the composition will be recomposed.
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // Apply the custom SimpleNotesAppTheme.
            // The theme (dark or light) is now reactively determined by the isDarkTheme state
            // collected from themeViewModel.
            SimpleNotesAppTheme(darkTheme = isDarkTheme) {
                // NavHost is the container for navigation destinations.
                // It requires a NavController and a startDestination route.
                NavHost(navController = navController, startDestination = "splash") {
                    // Define the "splash" screen destination.
                    composable("splash") {
                        SplashScreen(navController) // Display the SplashScreen composable.
                    }
                    // Define the "note_list" screen destination.
                    composable("note_list") {
                        NoteListScreen(
                            viewModel = noteViewModel,       // Pass the NoteViewModel.
                            themeViewModel = themeViewModel, // Pass the ThemeViewModel.
                            navController = navController   // Pass the NavController.
                        )
                    }
                    // Define the "add_note" screen destination.
                    composable("add_note") {
                        AddNoteScreen(navController, noteViewModel) // Display the AddNoteScreen composable.
                    }
                }
            }
        }
    }
}

