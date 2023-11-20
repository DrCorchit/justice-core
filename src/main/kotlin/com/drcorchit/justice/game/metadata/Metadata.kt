package com.drcorchit.justice.game.metadata

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject

interface Metadata: HasUri {
    override val parent: Game
    override val uri: Uri
        get() = parent.uri.extend("metadata")

    val start: Long
    val age: Long
    val lastModified: Long get() = parent.mechanics.maxOf { it.lastModified }

    //Location from which the game is saved and loaded.
    //May be an S3 or http url, or a file path.
    val path: String

    //Unique identifier for the game.
    val id: String

    //Client-visible identifier for the game.
    val name: String

    //Explains the game rules and objectives
    val description: String
    //Image that represents this game mode for the client
    val image: String
    val author: String
    val version: Version

    val isAutosaveEnabled: Boolean
    val isNotificationsEnabled: Boolean
    val isJoinable: Boolean get() = parent.getState().isJoiningEnabled && parent.players.size < parent.players.maxPlayerCount

    fun getProperty(property: String): Any?
    fun setProperty(property: String, value: Any)

    fun serialize(): JsonObject
    fun deserialize(info: JsonObject)
}