package com.drcorchit.justice.games.metadata

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.json.getBool
import com.google.gson.JsonObject

class JsonMetadata(override val game: Game, val json: JsonObject): Metadata {

    override val start: Long get() = json["start"].asLong
    override val age: Long get() = System.currentTimeMillis() - start
    override val lastModified: Long get() = json["lastModified"].asLong

    //Location from which the game is saved and loaded.
    //May be an S3 or http url, or a file path.
    override val path: String get() = json["path"].asString
    //Unique identifier for the game.
    override val id: String get() = json["id"].asString
    //Client-visible identifier for the game.
    override val name: String get() = json["name"].asString
    //Explains the game rules and objectives
    override val description: String get() = json["description"].asString
    override val image: String get() = json["image"].asString
    override val author: String get() = json["author"].asString
    override val version: Version get() = Version(json["version"].asString)

    override val isAutosaveEnabled: Boolean
        get() = json.getBool("autosaveEnabled", false)
    override val isNotificationsEnabled: Boolean
        get() = json.getBool("notificationsEnabled", false)
}