package com.drcorchit.justice.game.metadata

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.GameState
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.json.JsonUtils.getBool
import com.drcorchit.justice.utils.json.JsonUtils.getLong
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class JsonMetadata(parent: Game) : AbstractMetadata(parent) {
    private var json = JsonObject()

    override val start: Long get() = json["start"].asLong
    override val age: Long get() = System.currentTimeMillis() - start

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

    override fun getProperty(property: String): JsonElement? {
        return json[property]
    }

    override fun setProperty(property: String, value: Any) {
        if (value is JsonElement) {
            json.add(property, value)
        } else {
            json.add(property, JsonUtils.GSON.toJsonTree(value))
        }
    }

    override fun serialize(): JsonObject {
        json.addProperty("lastModified", lastModified)
        json.addProperty("seed", seed)
        json.addProperty("state", gameState.name)
        return json.deepCopy()
    }

    override fun sync(info: JsonObject) {
        json = info.deepCopy()

        //Init RNG
        random.setSeed(info.getLong("seed", Rng().getSeed()))

        //Init ID, if absent
        if (!json.has("id")) {
            json.addProperty("id", Game.generateId())
        }

        //Init Gamestate, if absent
        gameState = if (json.has("state")) {
            GameState.valueOf(json.get("state").asString)
        } else {
            GameState.PAUSED
        }

        if (!json.has("start")) {
            json.addProperty("start", System.currentTimeMillis())
        }
    }
}