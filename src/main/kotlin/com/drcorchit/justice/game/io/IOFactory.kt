package com.drcorchit.justice.game.io

import com.drcorchit.justice.game.Game

fun interface IOFactory {
    fun getIO(game: Game): IO
}