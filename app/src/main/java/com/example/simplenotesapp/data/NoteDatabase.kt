package com.example.simplenotesapp.data

// Import statements for Android Context (used to get application context for database builder)
// and Room persistence library components.
import android.content.Context
import androidx.room.Database // Annotation to mark the class as a Room database.
import androidx.room.Room // Helper class to build Room database instances.
import androidx.room.RoomDatabase // Base class for Room databases.
import com.example.simplenotesapp.model.Note // The Note entity that will be part of this database.

/**
 * The main database class for the application, built using Room.
 * This class defines the database configuration and serves as the main access point
 * to the persisted data.
 *
 * @Database annotation:
 *  - entities: Specifies the list of entity classes (tables) that are part of this database.
 *              Here, it's only the [Note] entity.
 *  - version:  The version number of the database schema. This must be incremented
 *              when you make schema changes (e.g., add/remove tables or columns).
 *              Migrations are typically required when incrementing the version.
 *  - exportSchema: If true, Room exports the database schema into a JSON file in your project.
 *                  This is useful for version control and understanding schema history.
 *                  Set to false here, often done for simpler projects or when not actively managing schema exports.
 */
@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() { // Must be abstract and extend RoomDatabase.

    /**
     * Abstract method to get an instance of the [NoteDao].
     * Room will generate the implementation for this method.
     * This provides access to the Data Access Object for interacting with the 'notes' table.
     *
     * @return An instance of [NoteDao].
     */
    abstract fun noteDao(): NoteDao

    /**
     * Companion object to provide a singleton instance of the [NoteDatabase].
     * This ensures that only one instance of the database is created throughout the application's lifecycle,
     * which is important for performance and data consistency.
     */
    companion object {
        // @Volatile annotation ensures that writes to INSTANCE are immediately visible to other threads.
        // This is important because INSTANCE can be accessed from multiple threads.
        @Volatile
        private var INSTANCE: NoteDatabase? = null // Holds the singleton instance of the database.

        /**
         * Gets the singleton instance of the [NoteDatabase].
         * If the instance doesn't exist, it creates one in a thread-safe manner.
         *
         * @param context The application context, used to create the database.
         * @return The singleton [NoteDatabase] instance.
         */
        fun getDatabase(context: Context): NoteDatabase {
            // Return the existing INSTANCE if it's not null.
            // This is a common pattern for lazy initialization (double-checked locking, effectively).
            return INSTANCE ?: synchronized(this) {
                // The 'synchronized' block ensures that only one thread can execute this code at a time,
                // preventing multiple instances of the database from being created concurrently.
                // 'this' refers to the Companion object instance.

                // Check again if INSTANCE is null inside the synchronized block.
                // This is necessary because another thread might have initialized it
                // between the first check and acquiring the lock.
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, // Use applicationContext to avoid memory leaks.
                    NoteDatabase::class.java,   // The database class.
                    "note_database"         // The name of the database file on disk.
                )
                    // .fallbackToDestructiveMigration()
                    // This strategy is used during database schema version upgrades.
                    // If a migration path is not defined for an old version to a new version,
                    // Room will destroy the existing database and recreate it.
                    // This means all data will be lost. For production apps, you'd typically
                    // implement proper migration paths using `addMigrations()`.
                    .fallbackToDestructiveMigration()
                    .build() // Builds the database instance.

                INSTANCE = instance // Assign the newly created instance to INSTANCE.
                instance            // Return the instance.
            }
        }
    }
}
