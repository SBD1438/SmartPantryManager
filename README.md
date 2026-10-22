# Smart Pantry Manager

## 1. App Description

Smart Pantry Manager is an Android application developed using Java and SQLite. It helps users manage pantry ingredients, monitor expiry dates, and discover recipes based on the ingredients they have available.

### Main Features

* **Pantry Management:** Add, view, edit, and delete pantry ingredients.
* **Expiry Reminders:** Display reminders for ingredients that have expired or are approaching their expiry dates.
* **Recipe Suggestions:** Suggest recipes based on available ingredients.
* **Recipe Details:** View recipe ingredients and preparation instructions.
* **Settings:** Enable or disable expiry reminders.
* **Data Persistence:** Save pantry items and settings using a local database.

## 2. Technologies Used

* **Programming Language:** Java
* **Development Environment:** Android Studio
* **Database:** SQLite
* **Build System:** Gradle
* **User Interface:** Android XML layouts

## 3. Database Choice

SQLite was chosen because it is built into Android and stores data locally on the device. It does not require a separate database server or an internet connection to access locally stored data.

SQLite is suitable for Smart Pantry Manager because it can store pantry ingredients, recipe information, recipe ingredients, and user settings in structured tables.

## 4. Project Structure

* `app/src/main/java/` contains the Java source code.
* `app/src/main/res/layout/` contains the XML layouts for the application screens.
* `app/src/main/res/drawable/` contains drawable resources, including the expiry warning background.
* `app/src/main/AndroidManifest.xml` declares the application's activities and configuration.
* `app/src/main/java/com/example/smartpantrymanager/database/DatabaseHelper.java` manages the SQLite database.
* `app/src/main/java/com/example/smartpantrymanager/utils/RecipeMatcher.java` matches available pantry ingredients against recipe requirements.

## 5. Setup and Run Instructions

### Requirements

* Android Studio
* Android SDK compatible with the project configuration
* An Android emulator or a compatible Android device


### Steps

1. Clone or download this repository from GitHub.
2. Open Android Studio.
3. Select **Open** and choose the downloaded project folder.
4. Allow Gradle to sync and download any required dependencies.
5. Start an Android emulator or connect an Android device with USB debugging enabled.
6. Select the `app` run configuration.
7. Click **Run** to build and launch Smart Pantry Manager.

## 6. Testing

The application was tested using an Android emulator. Testing included adding, editing, viewing, and deleting pantry ingredients, checking that data persists, changing expiry reminder settings, and verifying recipe suggestions against available ingredients.

## 7. Repository

GitHub repository: https://github.com/SBD1438/SmartPantryManager
