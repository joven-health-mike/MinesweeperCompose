# Minesweeper Compose
This is a modern Android application using JetPack Compose. It is a basic Minesweeper game, utilizing the open source project from my personal GitHub account, [mines-java](https://github.com/mikeburke106/mines-java) as the engine.

[Minesweeper Rules](https://minesweepergame.com/strategy/how-to-play-minesweeper.php)

# Activities
## Main Activity
Displays the main menu and handles clicks of menu items.

## Game Activity
Displays the game field, mines remaining, and a timer; handles clicks (reveal) and long-clicks (flag) on field zones.

The Game Activity is attached to the `GameViewModel.kt` view model. The view model manages interactions with the `mines-java` library through the `GameController.kt` wrapper. The view model's state is represented by `GameState.kt`.

When a game is over, a new game can be started from the Game Activity directly, either by clicking the revealed field, or by clicking "New Game" in the snackbar that appears when the game is over.

# Future of the Project
This project is open source and is free to use and modify however you like. You are free to contribute issues and pull requests, which may or may not be addressed.

New features are outlined in the [issues tab](https://github.com/joven-health-mike/MinesweeperCompose/issues). Please feel free to file a new issue for any feature requests.
