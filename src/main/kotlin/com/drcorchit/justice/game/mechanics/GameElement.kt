package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.Evaluable
import com.drcorchit.justice.utils.logging.HasUri
import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject

interface GameElement: HasUri {
    //A unique identifier for this element. Not client visible.
    //Contract: The key is unique and does not change under any circumstances.
    //Contract: parent().get(id).equals(this) is always true
    @get:DerivedField("The unique identifier of the element.")
    val id: String

    //Returns the parent game mechanic. Override forces the parent to be a GameMechanic, specifically.
    @get:DerivedField("The parent game mechanic.")
    override val parent: GameMechanic<*>

    //Call this method whenever the element is modified.
    @Evaluable("Notifies the engine that the element's data has changed.", true)
    fun touch() {
        parent.touch()
    }

    fun sync(info: JsonObject) {
        val universe = parent.parent.parent.types.universe
        val type = universe.getType(this::class to ImmutableList.of())
        type.sync(this, parent.parent.parent, info)
    }

    fun serialize(): JsonObject {
        val universe = parent.parent.parent.types.universe
        val type = universe.getType(this::class to ImmutableList.of())
        return type.serializeByReflection(this).asJsonObject
    }
}