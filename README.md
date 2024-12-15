# TrailForge

TrailForge is an Android application that allows users to create, view, and manage routes on a map. Users can sign up, log in, add new routes, and take photos.

## Features

- User authentication (sign up and log in)
- View routes on a map with markers and animations
- Add new routes by selecting points on the map
- Take photos and upload them with routes
- Display photos taken with routes
- Logout functionality

## Architecture
- **MVVM (Model-View-ViewModel):** The app utilizes the MVVM architecture pattern to separate UI, logic and data handling.
- **Layered Structure**: The project uses a layered architecture (data, model, ui and util) for improved code organization and testability
- **Reusable UI Components**: The application has reusable UI components, for consistent user interface, improving the overall maintainability of the project.

## Technologies Used

- Kotlin
- Java
- Gradle
- Supabase for backend services
    - Used for user authentication and secure storage of photos
- osmdroid for map functionalities
    - Used to display maps, add markers, lines, overlays and provide basic map operations.

## Images
<div style="display: flex; justify-content: center; gap: 10px;">

<img src="https://github.com/user-attachments/assets/d3f2584b-d724-49b6-8e80-83f3382d55cc" alt="Image 1" style="width: 45%; height: auto;">

<img src="https://github.com/user-attachments/assets/f673e99f-9b55-49c2-be23-8c45729e00b3" alt="Image 2" style="width: 45%; height: auto;">

</div>

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

   **Supabase Setup** (Optional)
    - Ensure you have enabled email authentication in your Supabase project.
    - Create a storage bucket named 'photos' for storing user-uploaded images.
4. Build and run the project on an Android device or emulator.

## Usage

- **Sign Up**: Create a new account using your email and password.
- **Log In**: Log in with your existing account.
- **View Routes**: View a list of saved routes.
- **Add Route**: Add a new route by selecting points on the map.
- **Take Photo**: Capture photos and associate them with routes.
- **Logout**: Log out from the application.

## License

This project is licensed under the MIT License
