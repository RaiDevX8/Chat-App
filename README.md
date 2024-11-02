
# ChatApp
ChatApp is a real-time chat application developed in Android Studio using Firebase as the backend. It supports user authentication, real-time messaging, profile management, and image uploads for a seamless chat experience.

## Features

- **User Authentication**: Email and password-based login and registration using Firebase Authentication.
- **Real-time Messaging**: Instant message delivery with Firebase Firestore as the real-time database.
- **User Profiles**: Users can set profile pictures, visible to other users in the chat.
- **Contact Sync**: Matches contacts from the user's device to registered users in the app.
- **Image Uploads**: Users can upload profile images and share them with contacts.

## Technologies Used

- **Android Studio**: IDE used for app development.
- **Java/Kotlin**: Programming languages used for app logic and UI.
- **Firebase Firestore**: NoSQL database to store chat messages and user profiles.
- **Firebase Authentication**: For secure user authentication.
- **Firebase Storage**: Used to store and retrieve profile images and other media.

## Getting Started

Follow these instructions to set up and run the project.

### Prerequisites

- **Android Studio**: Version Arctic Fox (or later) recommended.
- **Firebase Account**: Set up a project on [Firebase](https://firebase.google.com/).

### Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/RaiDevX8/ChatApp.git
   cd ChatApp
   ```

2. **Set up Firebase**:
   - Go to [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - Add an Android app in Firebase and follow the instructions to download the `google-services.json` file.
   - Place `google-services.json` in the app directory of your Android project.

3. **Enable Firebase Services**:
   - In your Firebase project, go to **Authentication** and enable **phone auth** authentication.
   - Go to **Firestore Database** and create a database in test mode.
   - Enable Firebase **Storage** for profile picture uploads.

4. **Sync Firebase to Android Studio**:
   - Open `build.gradle` (Project-level) and add the Firebase plugin if not already present:
     ```groovy
     classpath 'com.google.gms:google-services:4.3.8'
     ```

   - In `build.gradle` (App-level), apply the Google services plugin and add Firebase dependencies:
     ```groovy
     apply plugin: 'com.google.gms.google-services'
     ```

5. **Build the Project**:
   - Sync Gradle and build the project in Android Studio.

### Running the App

- Connect an Android device or start an emulator.
- Click **Run** in Android Studio, and the app should start on the selected device.

## Folder Structure

- `app/src/main/java`: Contains all Kotlin/Java source code files.
- `app/src/main/res`: Contains all resources, such as XML layout files, drawable assets, and values.

## Firebase Database Structure

- **Users Collection**: Stores user profiles with fields:
  - `userId`
  - `firstName`
  - `lastName`
  - `mobileNumber`
  - `profileImageUrl`

- **Messages Collection**: Stores chat messages with fields:
  - `senderId`
  - `receiverId`
  - `messageText`
  - `timestamp`

## Contributing

1. Fork the project.
2. Create a new branch (`git checkout -b feature/new-feature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/new-feature`).
5. Open a Pull Request.

   
 https://github.com/user-attachments/assets/ed18bc38-da1b-4219-b33f-80324bf579a4


## License

Distributed under the MIT License. See `LICENSE` for more information.

## Acknowledgments

- Firebase for backend services.
- Android Studio for the development platform.

