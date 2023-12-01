package com.drcorchit.justice.game.metadata

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject

interface Metadata: HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("metadata")

    @get:DerivedField("The date the game was started, in milliseconds since 1970.")
    val start: Long
    @get:DerivedField("The number of milliseconds elapsed since the start of the game.")
    val age: Long
    @get:DerivedField("The timestamp of the last modification to the gamestate.")
    val lastModified: Long get() = parent.mechanics.maxOf { it.lastModified }

    //Location from which the game is saved and loaded.
    //May be an S3 or http url, or a file path.
    val path: String

    //Unique identifier for the game.
    @get:DerivedField("The unique identifier of the game.")
    val id: String

    //Client-visible identifier for the game.
    @get:DerivedField("The client-visible name of the game. Not guaranteed to be unique.")
    val name: String

    //Explains the game rules and objectives
    @get:DerivedField("The client-visible description of the game.")
    val description: String
    //Image that represents this game mode for the client
    val image: String
    @get:DerivedField("The name of the game's author.")
    val author: String
    val version: Version

    @get:DerivedField("Indicates whether the game should be saved automatically.")
    val isAutosaveEnabled: Boolean
    @get:DerivedField("Indicates whether the game is configured to send network notifications to players.")
    val isNotificationsEnabled: Boolean
    @get:DerivedField("Indicates whether additional players may join the game.")
    val isJoinable: Boolean get() = parent.getState().isJoiningEnabled && parent.players.size < parent.players.maxPlayerCount

    fun getProperty(property: String): Any?
    fun setProperty(property: String, value: Any)

    fun serialize(): JsonObject
    fun deserialize(info: JsonObject)
}