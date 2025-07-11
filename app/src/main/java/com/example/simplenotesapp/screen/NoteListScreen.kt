package com.example.simplenotesapp.screen


// Import statements for various Jetpack Compose UI elements, layout modifiers,
// navigation, state management, and coroutines.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplenotesapp.model.Note
import com.example.simplenotesapp.viewmodel.NoteViewModel
import com.example.simplenotesapp.viewmodel.ThemeViewModel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color

/**
 * Defines the light color scheme for the application.
 * These colors are used when the app is in light theme mode.
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFFFFC1CC), // Light pink, typically for primary actions and highlights
    onPrimary = Color.Black,     // Color for text and icons displayed on top of the primary color
    surface = Color(0xFFFFF8F4), // Soft peach, for surfaces of components like cards, sheets
    onSurface = Color.Black      // Color for text and icons displayed on top of surface colors
)

/**
 * Defines the dark color scheme for the application.
 * These colors are used when the app is in dark theme mode.
 */
private val DarkColors = darkColorScheme(
    primary = Color(0xFF8B004D), // Deep magenta, for primary actions in dark mode
    onPrimary = Color.White,     // Color for text and icons on primary color in dark mode
    surface = Color(0xFF1E1E1E), // Dark gray, for surfaces in dark mode
    onSurface = Color.White      // Color for text and icons on surface color in dark mode
)

/**
 * Enum to represent the different screens or views within the note list context.
 * This helps in managing which set of notes (active or deleted) to display.
 */
enum class NoteScreen {
    ACTIVE,  // Represents the screen showing active, non-deleted notes
    DELETED  // Represents the screen showing recently deleted notes
}

/**
 * The main composable function for displaying the list of notes.
 * This screen includes a navigation drawer, a top app bar, a floating action button (for active notes),
 * and a list of notes. It handles switching between active and deleted notes views and theme toggling.
 *
 * @param navController Controller for navigating between different screens in the app.
 * @param viewModel The ViewModel responsible for note-related logic and data.
 * @param themeViewModel The ViewModel responsible for managing theme state (dark/light).
 */
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for using experimental Material 3 APIs
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    themeViewModel: ThemeViewModel
) {
    // Apply the MaterialTheme, dynamically choosing the color scheme based on the themeViewModel
    MaterialTheme(
        colorScheme = if (themeViewModel.isDarkTheme.collectAsState().value) DarkColors else LightColors
    ) {
        // State to keep track of the current view (Active or Deleted notes)
        // rememberSaveable ensures this state survives configuration changes (e.g., screen rotation)
        var currentScreen by rememberSaveable { mutableStateOf(NoteScreen.ACTIVE) }

        // LaunchedEffect to perform side effects when the composable enters the composition.
        // Unit as a key means it runs once when the composable is first displayed.
        LaunchedEffect(Unit) {
            currentScreen = NoteScreen.ACTIVE // Default to active notes screen
            viewModel.loadNotes(showDeleted = false) // Load active notes initially
        }

        // Observe the list of notes from the NoteViewModel as a Compose state
        val notes = viewModel.notes.collectAsState().value

        // State for managing the navigation drawer (open/closed)
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        // Coroutine scope tied to the composable's lifecycle, used for launching coroutines (e.g., for drawer, snackbar)
        val scope = rememberCoroutineScope()
        // State for managing the Snackbar display
        val snackbarHostState = remember { SnackbarHostState() }

        // ModalNavigationDrawer provides a standard navigation drawer pattern.
        ModalNavigationDrawer(
            drawerContent = { // Content of the drawer
                ModalDrawerSheet { // The actual sheet that slides out
                    // Title text for the drawer
                    Text(
                        "SimpleNotes",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Visual separator in the drawer
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    // Drawer item for navigating to the "Notes" (active notes) screen
                    DrawerItem(
                        label = "Notes",
                        onClick = {
                            currentScreen = NoteScreen.ACTIVE
                            scope.launch { // Launch a coroutine to handle UI changes
                                drawerState.close() // Close the drawer
                                viewModel.loadNotes(showDeleted = false) // Load active notes
                            }
                        }
                    )

                    // Drawer item for navigating to the "Recently Deleted" notes screen
                    DrawerItem(
                        label = "Recently Deleted",
                        onClick = {
                            currentScreen = NoteScreen.DELETED
                            scope.launch {
                                drawerState.close()
                                viewModel.loadNotes(showDeleted = true) // Load deleted notes
                            }
                        }
                    )

                    // Drawer item for toggling the application theme (light/dark)
                    DrawerItem(
                        label = "Toggle Theme",
                        onClick = {
                            themeViewModel.toggleTheme()
                            scope.launch { drawerState.close() } // Close the drawer after toggling
                        }
                    )
                }
            },
            drawerState = drawerState // Pass the drawer state to the component
        ) {
            // State to remember the last modified note and the action to undo its modification (for Snackbar)
            val lastModifiedNote = remember { mutableStateOf<Note?>(null) }
            val lastAction = remember { mutableStateOf<(() -> Unit)?>(null) }

            // Scaffold provides a standard layout structure (TopAppBar, FAB, content area).
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }, // Host for displaying Snackbars
                containerColor = Color(0xFFFFF8F4), // Sets a soft peach background for the Scaffold body
                topBar = {
                    TopAppBar(
                        title = {
                            // Display different titles based on whether viewing active or deleted notes
                            Text(
                                text = if (currentScreen == NoteScreen.DELETED) "Recently Deleted" else "Notes",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = { // Icon to open the navigation drawer
                            IconButton(onClick = {
                                scope.launch { drawerState.open() } // Open drawer on click
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        })
                },
                floatingActionButton = {
                    // Show FloatingActionButton only on the active notes screen to add new notes
                    if (currentScreen == NoteScreen.ACTIVE) {
                        FloatingActionButton(onClick = {
                            navController.navigate("add_note") // Navigate to the add note screen
                        }) {
                            Text("+") // Plus icon for the FAB
                        }
                    }
                }
            ) { paddingValues -> // Content area of the Scaffold, paddingValues contain insets for FAB/TopBar
                // LazyColumn efficiently displays a scrollable list of items.
                // It only composes and lays out items that are currently visible.
                LazyColumn(
                    contentPadding = paddingValues, // Apply padding from Scaffold
                    modifier = Modifier
                        .fillMaxSize()       // Take up all available space
                        .padding(16.dp)      // Add padding around the list itself
                ) {
                    // Iterate over the list of notes and create a NoteItem for each
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onDelete = { // Lambda function to handle note deletion
                                viewModel.softDeleteNote(note) // Perform soft delete in ViewModel
                                // Store the deleted note and the action to restore it (for UNDO)
                                lastModifiedNote.value = note
                                lastAction.value = {
                                    viewModel.restoreNote(note)
                                    viewModel.loadNotes(showDeleted = false) // Reload active notes after restore
                                }
                                // Launch a coroutine to show a Snackbar with an UNDO action
                                scope.launch {
                                    // Coroutine job for auto-dismissing the snackbar if UNDO is not clicked
                                    val job = launch {
                                        delay(2500) // Wait for 2.5 seconds
                                        snackbarHostState.currentSnackbarData?.dismiss() // Dismiss if still visible
                                    }
                                    // Show the Snackbar
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Note moved to Recently Deleted",
                                        actionLabel = "UNDO",
                                        withDismissAction = true, // Show a dismiss icon (X)
                                        duration = SnackbarDuration.Short // How long it stays if not dismissed
                                    )
                                    // If UNDO action was performed
                                    if (result == SnackbarResult.ActionPerformed) {
                                        lastAction.value?.invoke() // Execute the restore action
                                        job.cancel() // Cancel the auto-dismiss job
                                    }
                                }
                            },
                            // Provide a restore action only if on the "Recently Deleted" screen
                            onRestore = if (currentScreen == NoteScreen.DELETED) {
                                { // Lambda function to handle note restoration
                                    viewModel.restoreNote(note) // Perform restore in ViewModel
                                    viewModel.loadNotes(showDeleted = true) // Reload deleted notes
                                    // Store the restored note and the action to soft-delete it again (for UNDO)
                                    lastModifiedNote.value = note
                                    lastAction.value = {
                                        viewModel.softDeleteNote(note)
                                        // Optional: Switch to active notes screen after undoing a restore
                                        // viewModel.loadNotes(showDeleted = false)
                                        viewModel.loadNotes(showDeleted = true) // Reload deleted notes
                                    }
                                    // Launch a coroutine to show a Snackbar with an UNDO action
                                    scope.launch {
                                        val job = launch {
                                            delay(2500)
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                        }
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Note restored",
                                            actionLabel = "UNDO",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            lastAction.value?.invoke() // Execute the soft-delete action
                                            job.cancel()
                                        }
                                    }
                                }
                            } else null // No restore action if on the active notes screen
                        )
                        // Add some vertical space between note items
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Composable function to display a single note item in a Card.
 *
 * @param note The [Note] object to display.
 * @param onDelete Lambda function to be called when the delete action is triggered for this note.
 * @param onRestore Optional lambda function to be called when the restore action is triggered (used for deleted notes).
 */
@Composable
fun NoteItem(
    note: Note,
    onDelete: () -> Unit,
    onRestore: (() -> Unit)? = null // Nullable because restore is not always available
) {
    // Card provides a Material Design surface for displaying content and actions about a single subject.
    Card(
        modifier = Modifier.fillMaxWidth(), // Card takes the full width of its parent
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Use surface color from theme
        shape = RoundedCornerShape(12.dp), // Apply rounded corners to the card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Add shadow elevation
    ) {
        // Column arranges its children vertically.
        Column(modifier = Modifier.padding(16.dp)) { // Add padding inside the card
            // Display the note's title
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            // Add a small vertical space
            Spacer(modifier = Modifier.height(4.dp))
            // Display the note's content
            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
            // Add more vertical space before action buttons
            Spacer(modifier = Modifier.height(8.dp))

            // Row arranges its children horizontally. Used here for action buttons.
            Row {
                // Conditionally display either a "Restore" or "Delete" button.
                if (onRestore != null) { // If onRestore callback is provided (i.e., it's a deleted note)
                    Button(onClick = onRestore) {
                        Text("Restore")
                    }
                } else { // Otherwise (i.e., it's an active note)
                    Button(onClick = onDelete) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

/**
 * Composable function for creating a standard item in the navigation drawer.
 *
 * @param label The text to display for the drawer item.
 * @param onClick Lambda function to be executed when the drawer item is clicked.
 */
@Composable
fun DrawerItem(label: String, onClick: () -> Unit) {
    // Text composable that is made clickable and styled for a drawer item.
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()             // Make the item take the full width of the drawer
            .clickable(onClick = onClick) // Make it clickable
            .padding(16.dp),            // Add padding for touch targets and visual spacing
        style = MaterialTheme.typography.bodyLarge // Apply appropriate text style
    )
}
