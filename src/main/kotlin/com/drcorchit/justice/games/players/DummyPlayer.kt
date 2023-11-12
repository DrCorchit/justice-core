package com.drcorchit.justice.games.players

data class DummyPlayer(
    override val name: String,
    override var moderator: Boolean,
    override var human: Boolean) : Player {
}