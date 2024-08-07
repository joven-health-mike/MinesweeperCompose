package com.lordinatec.minesweepercompose.minesweeper.apis.controller

import com.mikeburke106.mines.api.controller.ViewController
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.api.view.MinesView

class AndroidFieldController : ViewController {
    var listener: GameControlStrategy.Listener? = null

    override fun onPositionClicked(x: Int, y: Int) {
        // tell model position clicked
    }

    override fun onPositionLongClicked(x: Int, y: Int) {
        // tell model position long clicked
    }

    override fun onInputValueClicked(input: MinesView.InputValue?) {
        // handle input
    }

    override fun setGameListener(listener: GameControlStrategy.Listener?) {
        this.listener = listener
    }

    override fun isGameOver(): Boolean {
        // check if game is over
        return false
    }
}