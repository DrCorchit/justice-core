package com.drcorchit.justice.game.players

import com.drcorchit.justice.lang.annotations.DataField

interface Player {
    @DataField("The unique ID of the player. Never changes.", false)
    var id: String

    @DataField("The screen name of the player. Subject to occasional change.", false)
    var name: String

    @DataField("Indicated whether the player has moderator privileges.", true, "false")
    var moderator: Boolean

    @DataField("Indicates whether the player is a human being and not AI.", true, "true")
    var human: Boolean
}