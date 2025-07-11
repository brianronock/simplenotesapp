package com.example.simplenotesapp.screen

// Import statements for Jetpack Compose UI elements, layout modifiers,
// state management (remember, mutableStateOf), and navigation.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Material 3 components like Button, OutlinedTextField.
import androidx.compose.runtime.* // Core Compose runtime functions like Composable, remember, mutableStateOf.
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier // Modifier for styling and layout adjustments.
import androidx.compose.ui.unit.dp // Density-independent pixels for specifying sizes.
import androidx.navigation.NavController // For navigating between screens.
import com.example.simplenotesapp.model.Note // Data model for a Note.
import com.example.simplenotesapp.viewmodel.NoteViewModel // ViewModel for note-related logic.

/**
 * Composable function for the screen used to add a new note or edit an existing one.
 * It provides text fields for the note's title and content, and a button to save the note.
 *
 * @param navController The NavController used for navigating back after saving the note.
 * @param viewModel The [NoteViewModel] instance used to interact with note data (e.g., inserting a new note).
 */
@Composable
fun AddNoteScreen(navController: NavController, viewModel: NoteViewModel) {
    // State for the note's title.
    // `remember` is used to keep the state across recompositions.
    // `mutableStateOf` creates an observable MutableState object.
    // When 'title' changes, any Composable reading it will be recomposed.
    var title by remember { mutableStateOf("") }

    // State for the note's content.
    var content by remember { mutableStateOf("") }

    // Column is a layout composable that arranges its children vertically.
    Column(
        modifier = Modifier
            .fillMaxSize() // The Column will take up the entire available screen space.
            .padding(16.dp) // Add padding around the content of the Column.
    ) {

        // OutlinedTextField is a Material Design text input field with an outlined border.
        OutlinedTextField(
            value = title, // The current text to display in the field.
            onValueChange = { title = it }, // Lambda called when the user types; updates the 'title' state.
            label = { Text("Title") },      // A label that is displayed inside or above the field.
            modifier = Modifier.fillMaxWidth() // The TextField will take the full width of its parent (the Column).
        )

        // Spacer adds empty space between composables, useful for layout.
        Spacer(modifier = Modifier.height(16.dp)) // Adds a vertical space of 16 dp.

        OutlinedTextField(
            value = content,
            onValueChange = { content = it }, // Updates the 'content' state.
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth() // Takes the full width.
                .height(200.dp)  // Sets a fixed height for the content field, allowing for multi-line input.
        )

        Spacer(modifier = Modifier.height(24.dp)) // Larger spacer before the button.

        // Button is a Material Design clickable button.
        Button(
            onClick = {
                // This lambda is executed when the button is clicked.
                // Check if both title and content are not blank (i.e., not empty and not just whitespace).
                if (title.isNotBlank() && content.isNotBlank()) {
                    // Create a new Note object with the current title and content.
                    // The Note model likely has default values for 'id', 'timestamp', and 'isDeleted'.
                    viewModel.insertNote(Note(title = title, content = content))

                    // Navigate back to the previous screen (presumably the note list).
                    // popBackStack() removes the current destination (AddNoteScreen) from the back stack.
                    navController.popBackStack()
                }
                // Optionally, you could add an else block here to show a Toast or Snackbar
                // if the user tries to save with empty fields.
            },
            modifier = Modifier.fillMaxWidth() // The Button will take the full width.
        ) {
            // The content of the Button, in this case, a Text composable.
            Text("Save Note")
        }
    }
}

