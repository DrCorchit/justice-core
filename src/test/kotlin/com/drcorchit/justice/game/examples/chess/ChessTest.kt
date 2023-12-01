package com.drcorchit.justice.game.examples.chess

import com.drcorchit.justice.game.TestGame
import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.toJson
import kotlin.test.Test

class ChessTest {
    @Test
    fun loadTest() {
        val chess = TestGame("src/test/resources/com/drcorchit/justice/games/examples/chess")
        chess.deserialize(chess.io.load("chess.json").toJson())
    }

    @Test
    fun saveTest() {
        val chess = TestGame("src/test/resources/com/drcorchit/justice/games/examples/chess")
        chess.deserialize(chess.io.load("chess.json").toJson())

        println(chess.serialize().prettyPrint())
    }
}