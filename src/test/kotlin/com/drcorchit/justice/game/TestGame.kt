package com.drcorchit.justice.game

import com.drcorchit.justice.game.io.LocalIO
import com.drcorchit.justice.game.monitoring.LatencyMonitoring
import com.drcorchit.justice.game.notifications.LogNotifying

class TestGame(basePath: String) : JusticeGame(LogNotifying, LocalIO(basePath), LatencyMonitoring())