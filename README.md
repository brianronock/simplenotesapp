

# 📝 SimpleNotesApp

A simple yet beautiful Jetpack Compose Notes app built with a modern architecture. This version includes local note storage using Room, Compose UI, MVVM pattern, splash screen, and a customizable sidebar with multiple menu options.

## 📱 Features Implemented

### ✅ Welcome Splash Screen
- A minimal splash screen welcoming users to the app.
- Displayed on app launch before navigating to main content.

### 🗒️ Empty Notes View
- If no notes are found, a friendly illustration and text prompt the user to add their first note.

### ➕ Add Notes
- A full-screen interface for creating a new note.
- Includes title and content input fields.
- Saves the note to a local Room database.

### 🧱 Notes Layout
- Responsive layout displaying notes in a staggered card grid.
- Includes title and a preview of content.
- Uses Jetpack Compose’s `LazyVerticalGrid` or `LazyColumn`.

### ☰ Sidebar Navigation Drawer
- Accessible via top-left menu icon.
- Menu items:
  - **Notes**: Default home screen.
  - **Theme**: Toggle between light and dark mode.
  - **Recently Deleted**: (Placeholder view).
  - **Archived Notes**: (Placeholder view).
- The theme switcher uses `MaterialTheme` for dynamic styling.

## 🧱 Architecture

- **Jetpack Compose** for UI.
- **Room** for local database.
- **MVVM** pattern using `ViewModel` and `StateFlow`.
- **Kotlin Coroutines** for async data handling.
- **KSP (Kotlin Symbol Processing)** for Room compiler.

## 📦 Dependencies

- `androidx.compose.*`
- `androidx.lifecycle.*`
- `androidx.room.*`
- `androidx.navigation.*`
- `kotlinx.coroutines`
- `material-icons-extended`

## 🎨 Design Alignment

The app currently reflects the general structure of the design:
- Soft pastel pinks and whites for the theme (can be tweaked in `Theme.kt`).
- Floating action button (FAB) for note creation.
- Sidebar now matches the mockup and includes:
  - Notes
  - Theme
  - Recently Deleted
  - Archived Notes

---

## 🧪 Next Steps

1. **Implement UI Consistency**: Ensure colors and UI elements match the provided design mockup, especially the top bar and iconography.
2. **Handle Recently Deleted and Archived Notes**: Create Room entities or status flags to support these views.
3. **Rich Text Formatting (Future Feature)**: Add basic formatting (bold, underline, etc.) support in note content.