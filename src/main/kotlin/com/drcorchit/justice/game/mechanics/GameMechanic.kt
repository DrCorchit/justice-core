package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.Evaluable
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject

interface GameMechanic<T : GameElement> : Iterable<T>, HasUri {
    @get:DerivedField("Returns the unique identifier of the mechanic.")
    val name: String

    override val parent: Mechanics

    override val uri: Uri get() = parent.uri.extend(name)

    @DataField("The timestamp the game was last modified.")
    var lastModified: Long

    @DerivedField("Returns the number of elements in the mechanic.")
    fun size(): Int

    @Evaluable("Returns true iff the mechanic contains an element with the given Uri.")
    fun has(id: String): Boolean

    //TODO: Generics? VirtualTypes?
    //@Evaluable("Returns the element with the given uri.")
    operator fun get(id: String): T

    //Some game elements have a default member. (E.g. Buffs, Resources, Features). This function is optional.
    //Game elements which do not have a default member should throw a NoSuchElementException when this is called
    @get:DerivedField("The default element, if any.")
    val defaultElement: T?

    //Call this method whenever the mechanic is modified. This should also update lastModified
    @Evaluable("Sets the last modified field to System.currentTimeMillis() and may clear cached fields.", true)
    fun touch()

    fun sync(json: TimestampedJson)

    fun serialize(): JsonObject
}