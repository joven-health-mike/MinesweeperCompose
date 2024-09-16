# Minesweeper Compose

This is a modern Android application using JetPack Compose. It is a basic Minesweeper game.

[Minesweeper Rules](https://minesweepergame.com/strategy/how-to-play-minesweeper.php)

# Activities

## Main Activity

Displays the main menu and handles clicks of menu items.

## Game Activity

Displays the game field, mines remaining, and a timer; handles clicks (reveal) and long-clicks (
flag) on the field.

The Game Activity is attached to the `GameViewModel.kt` view model. The view model manages
interactions with the `GameController.kt`. The view model's
state is represented by `GameState.kt`.

When a game is over, a new game can be started from the Game Activity directly, either by clicking
the revealed field, or by clicking "New Game" in the snackbar that appears when the game is over.

## Stats Activity

Displays the user's stats, including the number of games won, and lost, as well as the best win
time.

## Settings Activity

Allows the user to change the game settings.

# Architecture

This project uses the MVVM architecture pattern. The `GameViewModel.kt` is the view model for the
Game Activity. The view model forwards user actions to `GameController.kt`, which publishes events
listened to by `GameStateEventConsumer.kt` in order to update the UI state in `GameState.kt`.

## Model

The model design consists of the following classes:

- `Field.kt`: Interface that handles basic functionality of the game field (e.g., revealing,
  flagging, and
  generating mines).
- `AndroidField.kt`: Implementation of the `Field` interface that handles basic functionality of the
  game.
- `Coordinate.kt`: Represents a coordinate on the game field.
- `CoordinateTranslator.kt`: Translates XY coordinates to indices based on the field size.
- `GameController.kt`: Manages interactions with the field and publishes game events.
- `Timer.kt`: Represents a timer that can be started, stopped, and reset.
- `TimerLifecycleObserver.kt`: Listens for lifecycle events and starts/stops the timer accordingly.

## View Model

The view model design consists of the following classes:

- `GameViewModel.kt`: Manages interactions with the `GameController.kt`.
- `GameState.kt`: Represents the UI state of the game.
- `GameStateEventConsumer.kt`: Listens for game events and updates the UI state accordingly.
- `TileViewClickListener.kt`: Handles functionality for clicks and long-clicks on the game field,
  and dispatches those actions to the view model.

## Views

The view design consists of the following classes:

- `GameView.kt`: Top-level view that displays the game field, mines remaining, and timer.
- `FieldView.kt`: Displays the game field itself.
- `TileArray.kt`: Represents the game field as a 2D array of tiles.
- `TileView.kt`: Represents a single tile on the game field.

## Game Events

The game events design consists of the following classes:

- `GameEvent.kt`: Represents a game event. The following events are supported:
    - `GameCreated`: The game has been created.
    - `GameWon`: The game has been won.
    - `GameLost`: The game has been lost.
    - `PositionCleared`: A tile has been cleared.
    - `PositionFlagged`: A tile has been flagged.
    - `PositionUnflagged`: A tile has been unflagged.
    - `PositionExploded`: A tile has been exploded.
    - `FieldReset`: The field has been reset for a new game.
    - `TimeUpdate`: The timer has been updated.
- `GameEventPublisher.kt`: Publishes game events.

# Testing

This project uses JUnit 4 for unit testing and Espresso for UI testing.

## Unit Tests

Unit tests are found in the `test` directory. Most important classes are tested, including the
`GameController.kt`, `GameStateEventConsumer.kt`, and `GameViewModel.kt`.

## UI Tests

UI tests are found in the `androidTest` directory. The `GameActivityTest.kt` tests the Game Activity
and the `StatsUpdaterTest.kt` tests the functionality of updating stats in Shared Prefs.

# Dependency Injection

This project uses Hilt for dependency injection. The `@HiltAndroidApp` annotation is used in the
`MinesweeperApplication.kt` file to initialize Hilt.

# Future of the Project

This project is open source and is free to use and modify however you like. You are free to
contribute issues and pull requests, which may or may not be addressed.

New features are outlined in
the [issues tab](https://github.com/joven-health-mike/MinesweeperCompose/issues). Please feel free
to file a new issue for any feature requests.
