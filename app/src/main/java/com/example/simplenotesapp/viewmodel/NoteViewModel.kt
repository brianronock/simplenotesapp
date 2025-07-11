package com.example.simplenotesapp.viewmodel

// Import statements for AndroidX Lifecycle components (ViewModel, ViewModelProvider, viewModelScope)
// and Kotlin Coroutines features (Flow, StateFlow, launch).
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simplenotesapp.model.Note // Data model for a Note.
import com.example.simplenotesapp.data.NoteDao // Data Access Object for interacting with the note database.
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing UI-related data for notes in a lifecycle-conscious way.
 * This class is responsible for preparing and managing the data for an Activity or a Fragment.
 * It also handles communication with the data layer (NoteDao) for CRUD operations.
 *
 * @property noteDao The Data Access Object for notes, used to interact with the database.
 */
class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    // _notes is a private MutableStateFlow that holds the current list of notes.
    // MutableStateFlow is a state-holder observable flow that emits the current and new state updates to its collectors.
    // It's initialized with an empty list.
    private val _notes = MutableStateFlow<List<Note>>(emptyList())

    // notes is a public immutable StateFlow that exposes the list of notes to the UI.
    // UI components can collect from this Flow to observe changes in the notes list.
    // .asStateFlow() makes the MutableStateFlow read-only from the outside.
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    // A private variable to keep track of whether the currently loaded notes are soft-deleted ones.
    private var showingDeleted = false

    /**
     * The init block is executed when an instance of NoteViewModel is created.
     * It calls loadNotes() to fetch the initial list of active notes.
     */
    init {
        loadNotes() // Load active notes by default
    }

    /**
     * Loads notes from the database based on the 'showDeleted' flag.
     * Updates the _notes StateFlow with the fetched notes.
     *
     * @param showDeleted If true, loads soft-deleted notes; otherwise, loads active notes. Defaults to false.
     */
    fun loadNotes(showDeleted: Boolean = false) {
        // Update the internal state of which type of notes are being shown.
        this.showingDeleted = showDeleted
        // Launch a new coroutine in the viewModelScope.
        // viewModelScope is a CoroutineScope tied to this ViewModel's lifecycle.
        // Coroutines launched in this scope are automatically cancelled when the ViewModel is cleared.
        viewModelScope.launch {
            // Determine which Flow to collect from the DAO based on the showDeleted flag.
            val flow = if (showDeleted) {
                noteDao.getDeletedNotes() // Flow for deleted notes
            } else {
                noteDao.getActiveNotes()  // Flow for active notes
            }
            // Collect emissions (list of notes) from the chosen Flow.
            flow.collect { notesList ->
                // Update the value of _notes, which will notify its collectors (e.g., the UI).
                _notes.value = notesList
            }
        }
    }

    /**
     * Inserts a new note into the database.
     * This operation is performed asynchronously in a coroutine.
     *
     * @param note The [Note] object to be inserted.
     */
    fun insertNote(note: Note) {
        viewModelScope.launch {
            noteDao.insertNote(note)
        }
    }

    /**
     * Marks a note as "soft-deleted" by setting its 'isDeleted' flag to true.
     * The note is updated in the database, not physically removed.
     * This operation is performed asynchronously in a coroutine.
     *
     * @param note The [Note] object to be soft-deleted.
     */
    fun softDeleteNote(note: Note) = viewModelScope.launch {
        // Create a copy of the note with the isDeleted flag set to true.
        noteDao.updateNote(note.copy(isDeleted = true))
    }

    /**
     * Restores a soft-deleted note by setting its 'isDeleted' flag to false.
     * The note is updated in the database.
     * This operation is performed asynchronously in a coroutine.
     *
     * @param note The [Note] object to be restored.
     */
    fun restoreNote(note: Note) = viewModelScope.launch {
        // Create a copy of the note with the isDeleted flag set to false.
        noteDao.updateNote(note.copy(isDeleted = false))
    }

    /**
     * Permanently deletes a note from the database.
     * This operation is performed asynchronously in a coroutine.
     *
     * @param note The [Note] object to be permanently deleted.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
}

/**
 * Factory class for creating instances of [NoteViewModel].
 * This is necessary because NoteViewModel has a constructor parameter (noteDao),
 * and ViewModelProvider needs a way to instantiate it.
 *
 * @property noteDao The Data Access Object for notes, to be passed to the NoteViewModel.
 */
class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `modelClass` (ViewModel).
     *
     * @param modelClass A class whose instance is requested, which must be a subclass of ViewModel.
     * @return A newly created ViewModel.
     * @throws IllegalArgumentException if the modelClass is not assignable from NoteViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is NoteViewModel or a subclass of it.
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            // Suppress the unchecked cast warning, as we've confirmed the type.
            @Suppress("UNCHECKED_CAST")
            // Create and return an instance of NoteViewModel, passing the noteDao.
            return NoteViewModel(noteDao) as T
        }
        // If the requested ViewModel class is unknown, throw an exception.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
