# zzzPizzza

An Android pizza picker built with Jetpack Compose: an animated splash screen, a pizza carousel, size and quantity
selection with a live total price, and a zoomed-in view of the pizza.

## Demo

https://github.com/user-attachments/assets/f28c535f-3d68-4f0b-b5b8-9b06bdf0c8ea

## Features

### Splash screen

- The pizza is revealed slice by slice with a circular sweep animation.
- The pizza list is loaded from the API and cached in a local Room database.
- The app moves on to the catalog only when both the animation and the loading have finished.
- On a network error the app shows a message and retries the request every minute.

### Pizza catalog

- **Carousel**: tap a pizza on either side to bring it to the center.
- **Size picker**: S, M and L buttons along an arc. The pizza image animates to the selected size, and each size has
  its own price from the API.
- **Quantity**: the "+" and "−" buttons change the count from 1 to 99.
- **Total price**: the selected size's price × the quantity, updated instantly.
- **Zoom**: tap the center pizza to zoom in on the ingredients, drag to look around, tap again to zoom out.
- The "Back", "Favorite" and "Add" buttons are static and only show a toast.

## Tech stack

- Kotlin, coroutines and Flow
- Jetpack Compose, Material 3
- Hilt for dependency injection
- Retrofit and Moshi for networking
- Room for the local cache
- Coil 3 for image loading
- Navigation 3 with [nav3router](https://github.com/arttttt/nav3router)

Min SDK 24, target SDK 37.

## Architecture

The code is split into three layers under `com.example.zzzpizzza`:

```
data/          Retrofit API and DTOs, Room database and entities, mappers, source implementations, DI modules
domain/        Domain entities, source interfaces, use cases, ResultValue
presentation/  Compose screens and components, ViewModels, UI models, theme
```

- The domain layer has no Android dependencies; data and presentation depend on it.
- Each screen has a ViewModel built on `BaseViewModel`. It keeps a UI `State` for the screen, an `InternalState` for
  data the UI does not render, receives `Event`s from the UI and sends one-off `Label`s (such as error messages)
  back to it.

## Build and run

Open the project in Android Studio and run the `app` configuration, or build from the command line:

```
./gradlew :app:assembleDebug
```

## Tests

Local unit tests use JUnit 4, MockK, kotlinx-coroutines-test, Truth and Turbine. They cover both ViewModels, the
remote and local data sources, the DTO and entity mappers, and the size-button layout math.

```
./gradlew :app:testDebugUnitTest
```

## Credits

The UI follows a design from a third-party test assignment. Pizza data and images come from that assignment's
public API.
