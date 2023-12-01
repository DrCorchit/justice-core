package com.drcorchit.justice.game.examples.chess

import com.drcorchit.justice.game.mechanics.AbstractElement
import com.drcorchit.justice.game.mechanics.AbstractMechanic
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.annotations.DataField
import com.google.gson.JsonObject

class Colors(parent: Mechanics, name: String) : AbstractMechanic<Colors.ChessColor>(parent, name) {

    inner class ChessColor(id: String) : AbstractElement(id) {
        override val parent: Colors = this@Colors

        @DataField("The player which controls pieces of this color.", false)
        lateinit var player: Player

        @DataField("The name of the color.", false)
        lateinit var name: String

        @DataField("Indicates whether the player has retained the ability to castle kingside.", true, "true")
        var canCastleKingside: Boolean = true

        @DataField("Indicates whether the player has retained the ability to castle queenside.", true, "true")
        var canCastleQueenside: Boolean = true

        @DataField("Indicates the direction which pawns move down the board.", false, "1")
        var pawnDirection: Int = 1
    }

    override fun create(id: String, info: JsonObject): ChessColor {
        return ChessColor(id)
    }
}