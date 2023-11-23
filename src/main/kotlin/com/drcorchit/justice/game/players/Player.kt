package com.drcorchit.justice.game.players

import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.HasType

interface Player : HasType<Player> {
    @DataField("The unique ID of the player. Never changes.", false)
    var id: String

    @DataField("The screen name of the player. Subject to occasional change.", false)
    var name: String

    @DataField("Indicated whether the player has moderator privileges.", true)
    var moderator: Boolean

    @DataField("Indicates whether the player is a human being and not AI.", true)
    var human: Boolean

    override fun getType(): Type<Player> {
        return PlayerType
    }
}