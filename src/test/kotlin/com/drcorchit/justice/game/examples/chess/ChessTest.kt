package com.drcorchit.justice.game.examples.chess

import com.drcorchit.justice.game.JusticeGame
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.json.info
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions
import java.io.File
import kotlin.test.Test

class ChessTest {

    val chessPath = "src/test/resources/com/drcorchit/justice/games/examples/chess"

    @Test
    fun loadTest() {
        val chess = JusticeGame.localGame(chessPath)

        Assertions.assertEquals("Bobby Fisher", chess.players.getPlayer("user1")!!.name)
        Assertions.assertEquals("Gary Kasparov", chess.players.getPlayer("user2")!!.name)
    }

    @Test
    fun saveTest() {
        val chess = JusticeGame.localGame(chessPath)

        val output = chess.serialize()
        //println(output.prettyPrint())
        Assertions.assertEquals(3, output.getAsJsonObject("mechanics").size())
        Assertions.assertEquals(2, output.getAsJsonObject("players").getAsJsonArray("roster").size())

        val subfolder = "$chessPath/testGame"
        chess.io.path = subfolder
        chess.io.save()
        Assertions.assertTrue(File("$subfolder/game.json").exists())
        Assertions.assertTrue(File("$subfolder/mechanics").isDirectory)
        chess.io.delete()
        Assertions.assertFalse(File("$subfolder/game.json").exists())
    }

    @Test
    fun eventTest() {
        val chess = JusticeGame.localGame(chessPath)
        val bobbyFisher = chess.players.getPlayer("Bobby Fisher")!!
        val piece = chess.mechanics.get<Pieces>("pieces")["0"]
        Assertions.assertEquals("1,1", piece.position.toString())
        val info = JsonObject()
        info.addProperty("event", "move")
        info.addProperty("piece", "pieces.0")
        info.addProperty("destination", "0,0")
        chess.events.post(bobbyFisher, info)
        Assertions.assertEquals("0,0", piece.position.toString())
    }

    @Test
    fun summarizeTest() {
        val chess = JusticeGame.localGame(chessPath)

        val expectedSummary = JsonUtils.parseFromFile("$chessPath/summary.json")!!.info.asJsonObject
        //These fields won't match (They both depend on system clock)
        expectedSummary.remove("lastModified")
        expectedSummary.remove("id")
        val actualSummary = chess.summarize()
        actualSummary.remove("lastModified")
        actualSummary.remove("id")
        Assertions.assertEquals(expectedSummary, actualSummary)
    }
}