package com.example.simplenotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.simplenotesapp.screen.AddNoteScreen
import com.example.simplenotesapp.screen.NoteListScreen
import com.example.simplenotesapp.ui.theme.SimpleNotesAppTheme
import com.example.simplenotesapp.viewmodel.NoteViewModel
import com.example.simplenotesapp.screen.SplashScreen
import com.example.simplenotesapp.viewmodel.ThemeViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.example.simplenotesapp.data.NoteDatabase
import com.example.simplenotesapp.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()  // Moved inside
            val navController: NavHostController = rememberNavController()
            val context = LocalContext.current
            val db = NoteDatabase.getDatabase(context)
            val noteDao = db.noteDao()
            val factory = NoteViewModelFactory(noteDao)

            val noteViewModel: NoteViewModel = viewModel(factory = factory)
            SimpleNotesAppTheme(darkTheme = themeViewModel.isDarkTheme.value) {
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    composable("note_list") {
                        NoteListScreen(
                            viewModel = noteViewModel,
                            themeViewModel = themeViewModel,
                            navController = navController
                        )
                    }
                    composable("add_note") {
                        AddNoteScreen(navController, noteViewModel)
                    }
                }
            }
        }
    }
}