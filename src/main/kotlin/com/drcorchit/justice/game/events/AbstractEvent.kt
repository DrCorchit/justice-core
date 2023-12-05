package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.utils.Version

abstract class AbstractEvent(
    final override val parent: Events,
    final override val name: String,
    final override val description: String,
    final override val version: Version,
    final override val parameters: Parameters
) : Event {
    final override val uri = parent.uri.extend(name)
}