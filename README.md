# TrailForge

TrailForge is an Android application that allows users to create, view, and manage routes on a map. Users can sign up, log in, add new routes, and take photos.

## Features

- User authentication (sign up and log in)
- View routes on a map
- Add new routes
- Take photos
- Logout functionality

## Technologies Used

- Kotlin
- Java
- Gradle
- Supabase for backend services
- osmdroid for map functionalities

## Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/mirohee/trailforge.git
    cd trailforge
    ```

2. Open the project in Android Studio.

3. Add your Supabase credentials to `local.properties`:
    ```kotlin
    // In your local.properties file add
    SUPABASE_URL=[YOUR SUPABASE URL]
    SUPABASE_API_KEY=[YOUR SUPABASE API KEY]
    ```

4. Build and run the project on an Android device or emulator.

## Usage

- **Sign Up**: Create a new account using your email and password.
- **Log In**: Log in with your existing account.
- **View Routes**: View a list of routes on the map.
- **Add Route**: Add a new route by selecting points on the map.
- **Take Photo**: Capture photos and associate them with routes.
- **Logout**: Log out from the application.

## License

This project is licensed under the MIT License
