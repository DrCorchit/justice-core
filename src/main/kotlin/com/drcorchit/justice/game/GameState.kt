package com.drcorchit.justice.game

enum class GameState(val isJoiningEnabled: Boolean, val isEventsEnabled: Boolean, val isSavingEnabled: Boolean) {
    //Transitions to "open" when minPlayers is reached
    STAGING(true, false, false),
    //Transitions to "closed" when maxPlayers is reached
    OPEN(true, true, true),
    CLOSED(false, true, true),
    //The difference between paused and retired games is that retired games will eventually be garbage collected
    RETIRED(false, false, false),
    PAUSED(false, false, true);

    companion object {
        //Retired games may be cleaned up at any time 1 week after their last modified date
        const val RETIRED_LIFESPAN = (7 * 24 * 60 * 60 * 1000).toLong()
    }
}