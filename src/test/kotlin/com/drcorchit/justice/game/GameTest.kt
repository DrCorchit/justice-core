package com.drcorchit.justice.game

import com.drcorchit.justice.game.monitoring.LatencyMonitoring
import com.drcorchit.justice.game.notifications.LogNotifying
import com.drcorchit.justice.game.saving.LocalSaving
import com.drcorchit.justice.utils.IOUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.io.File

class GameTest {

    @Test
    fun createTest() {
        val game = JusticeGame(LogNotifying(), LocalSaving(PATH), LatencyMonitoring())
    }

    @AfterAll
    fun cleanup() {
        IOUtils.deleteRecursively(File(PATH))
    }

    companion object {
        val PATH = "src/test/kotlin/com/drcorchit/justice/game/test"
    }
}