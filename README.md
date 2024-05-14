# Cabify Mobile Challenge App

This project is an implementation of the Cabify Mobile Challenge. It demonstrates the use of modern Android development practices including Jetpack Compose, MVVM architecture, Clean Architecture principles, Kotlin Flows and Coroutines, Room for local database management, Retrofit for network requests, and Koin for dependency injection. The project also includes comprehensive unit tests for use cases, repository implementations, and the ViewModel.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Setup](#setup)
- [Unit Tests](#unit-tests)
- [Caching](#caching)
- [Contributing](#contributing)
- [License](#license)

## Features

- Display a list of products from an API.
- Add and remove products from the cart.
- Apply discount rules to cart items.
- Cache discount rules in a raw JSON file.

## Architecture

The project follows the MVVM (Model-View-ViewModel) architecture and Clean Architecture principles, ensuring a clear separation of concerns and high testability.

- **Model**: Represents the data layer, including entities, repositories, and data sources (Room and Retrofit).
- **View**: Represents the UI layer, built with Jetpack Compose.
- **ViewModel**: Manages UI-related data and business logic, leveraging Kotlin Flows for reactive programming.

## Technologies Used

- **Jetpack Compose**: For building the UI.
- **Kotlin Flows and Coroutines**: For asynchronous programming and reactive streams.
- **Room**: For local database management.
- **Retrofit**: For network requests.
- **Koin**: For dependency injection.
- **Mockito**: For mocking dependencies in unit tests.
- **Turbine**: For testing Kotlin Flows.

## Setup

1. **Clone the repository**:
    ```sh
    git clone https://github.com/Stan-Balabushevich/cabify-mobile-challenge-app.git
    cd cabify-mobile-challenge-app
    ```

2. **Open the project in Android Studio**:
    - Ensure you have the latest version of Android Studio installed.

3. **Build the project**:
    - Let Gradle sync and build the project.

4. **Run the project**:
    - You can run the app on an emulator or a physical device.

## Unit Tests

The project includes comprehensive unit tests covering use cases, repository implementations, and ViewModel. The tests use fakes for use cases and repository, and Mockito for testing the ViewModel.

### Running Unit Tests

1. **Run tests from Android Studio**:
    - Navigate to the `test` folder and right-click to run the tests.

2. **Run tests from the command line**:
    ```sh
    ./gradlew test
    ```

## Caching

The project includes a mechanism to cache JSON data using a raw file. This ensures that discount rules are available even when the app is offline or the network request fails.

## Contributing

Contributions are welcome! If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.

1. **Fork the repository**.
2. **Create a feature branch**.
    ```sh
    git checkout -b feature-branch
    ```
3. **Commit your changes**.
    ```sh
    git commit -m "Description of changes"
    ```
4. **Push to the branch**.
    ```sh
    git push origin feature-branch
    ```
5. **Create a new Pull Request**.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
