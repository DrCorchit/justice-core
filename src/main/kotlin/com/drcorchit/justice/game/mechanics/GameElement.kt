package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.JFunction
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject

interface GameElement: HasUri {
    //A unique identifier for this element. Not client visible.
    //Should rely on a class with equals and hashcode defined.
    //Contract: The key is unique and does not change under any circumstances.
    //Contract: parent().get(uri).equals(this) is always true
    @DataField("The unique identifier of the element.")
    override val uri: Uri

    //Returns the parent game mechanic
    @get:DerivedField("The parent game mechanic.")
    override val parent: GameMechanic<*>

    //Call this method whenever the element is modified.
    @JFunction("Notifies the engine that the element's data has changed.", true)
    fun touch() {
        parent.touch()
    }

    fun sync(info: JsonObject) {
        val universe = parent.parent.parent.types.universe
        val type = universe.getType(this::class to ImmutableList.of())
        type.sync(this, parent.parent.parent, info)
    }
}