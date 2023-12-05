package com.drcorchit.justice.game

import com.drcorchit.justice.game.evaluation.JusticeTypes
import com.drcorchit.justice.game.events.EventsImpl
import com.drcorchit.justice.game.io.IOFactory
import com.drcorchit.justice.game.io.LocalIO
import com.drcorchit.justice.game.mechanics.MechanicsImpl
import com.drcorchit.justice.game.metadata.JsonMetadata
import com.drcorchit.justice.game.monitoring.LatencyMonitoring
import com.drcorchit.justice.game.monitoring.Monitoring
import com.drcorchit.justice.game.notifications.LogNotifying
import com.drcorchit.justice.game.notifications.Notifying
import com.drcorchit.justice.game.players.PlayersImpl

//Justice game is able to fulfill all the responsibilities of a game on its own,
// except notifying players and saving, which typically require network calls.
class JusticeGame(
    override val notifying: Notifying,
    override val monitoring: Monitoring,
    ioFactory: IOFactory
    ) : Game {
    override val players = PlayersImpl(this)
    override val mechanics = MechanicsImpl(this)
    override val events = EventsImpl(this)
    override val metadata = JsonMetadata(this)
    override val types = JusticeTypes(this)
    override val io = ioFactory.getIO(this)

    companion object {
        fun localGame(path: String): JusticeGame {
            val game = JusticeGame(LogNotifying, LatencyMonitoring()) { LocalIO(path, it) }
            game.io.load()
            return game
        }
    }
}