package com.example.simplenotesapp.data

// Import statements for Room persistence library annotations
// and Kotlin Coroutines Flow for asynchronous data streams.
import androidx.room.*
import com.example.simplenotesapp.model.Note // Data model for a Note.
import kotlinx.coroutines.flow.Flow // Used for observing data changes asynchronously.

/**
 * Data Access Object (DAO) for the 'notes' table.
 * This interface defines the database interactions (queries, inserts, updates, deletes)
 * for [Note] entities. Room will generate the implementation for these methods.
 * DAOs are the main way to interact with your app's persisted data.
 */
@Dao // Marks the interface as a Room DAO.
interface NoteDao {

    /**
     * Retrieves all notes from the 'notes' table, ordered by timestamp in descending order (newest first).
     * This method returns a [Flow] of a list of notes. The Flow will automatically emit a new list
     * whenever the data in the 'notes' table changes, allowing the UI to reactively update.
     *
     * @return A Flow emitting a list of all [Note] objects.
     */
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    /**
     * Retrieves all active (not soft-deleted) notes from the 'notes' table.
     * Active notes are those where the 'isDeleted' flag is 0 (false).
     * The results are ordered by timestamp in descending order (newest first).
     *
     * @return A Flow emitting a list of active [Note] objects.
     */
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getActiveNotes(): Flow<List<Note>>

    /**
     * Retrieves all soft-deleted notes from the 'notes' table.
     * Deleted notes are those where the 'isDeleted' flag is 1 (true).
     * The results are ordered by timestamp in descending order (newest first).
     *
     * @return A Flow emitting a list of soft-deleted [Note] objects.
     */
    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY timestamp DESC")
    fun getDeletedNotes(): Flow<List<Note>>

    /**
     * Inserts a new note into the 'notes' table.
     * If a note with the same primary key already exists, it will be replaced
     * due to `onConflict = OnConflictStrategy.REPLACE`.
     * This is a suspend function, meaning it should be called from a coroutine
     * or another suspend function to perform the operation asynchronously.
     *
     * @param note The [Note] object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    /**
     * Updates an existing note in the 'notes' table.
     * The note is identified by its primary key.
     * This is a suspend function, designed for asynchronous execution.
     *
     * @param note The [Note] object with updated values.
     */
    @Update
    suspend fun updateNote(note: Note)

    /**
     * Deletes a note from the 'notes' table.
     * The note is identified by its primary key.
     * This is a suspend function, designed for asynchronous execution.
     *
     * @param note The [Note] object to be deleted.
     */
    @Delete
    suspend fun deleteNote(note: Note)
}
