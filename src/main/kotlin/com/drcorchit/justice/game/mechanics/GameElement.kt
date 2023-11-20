package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.JFunction
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject

interface GameElement: HasUri {
    //The name of the object as seen by the client
    //The name is encouraged to be unique. It may also change from time to time.
    @DataField("The client-facing name of the game element.", true)
    val name: String

    //A unique identifier for this element. Not client visible.
    //Should rely on a class with equals and hashcode defined.
    //Contract: The key is unique and does not change under any circumstances.
    //Contract: parent().get(uri).equals(this) is always true
    @DataField("The unique identifier of the element.")
    override val uri: Uri
        get() = parent.uri.extend(name)

    //A short description of the object visible to the client
    @DataField("A client-facing description of the element.", true)
    val description: String

    //Returns the parent game mechanic
    @get:DerivedField("The parent game mechanic.")
    override val parent: GameMechanic<*>

    //Call this method whenever the element is modified.
    @JFunction("Notifies the engine that the element's data has changed.", true)
    fun touch() {
        parent.touch()
    }

    fun sync(info: JsonObject)
}