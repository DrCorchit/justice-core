package com.drcorchit.justice.game.io

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.lastModified
import com.google.gson.JsonElement
import com.google.gson.JsonObject

interface IO {

    val basePath: String

    //Saving may be triggered after creation, after a state change, or after an api call.
    fun save(game: Game): Result {
        val output = JsonObject()
        output.add("metadata", game.metadata.serialize())
        output.add("players", game.players.serialize())
        output.add("events", game.events.serialize())
        output.add("mechanics", game.mechanics.serialize())

        var result: Result = saveJson("$basePath/game.json", output)
        game.mechanics.forEach {
            val path = "$basePath/${it.name}"
            val json = it.serialize()
            result = result.and(saveJson(path, json))
        }

        return Result.succeed()
    }

    //Loads the game state from Json
    fun load(game: Game): Result {
        val json = loadJson("$basePath/game.json")
        val info = json.info.asJsonObject
        val metadataInfo = info.getAsJsonObject("metadata").deepCopy()
        val playersInfo = info.getAsJsonObject("players").deepCopy()
        val eventsInfo = info.getAsJsonObject("events").deepCopy()
        val mechanicsInfo = info.getAsJsonObject("mechanics").deepCopy()

        game.metadata.deserialize(metadataInfo)
        game.players.deserialize(playersInfo)
        game.events.deserialize(eventsInfo)
        game.mechanics.deserialize(mechanicsInfo, json.lastModified)

        return Result.succeed()
    }

    //Irreversibly deletes files or database entries associated with the game.
    fun delete(game: Game): Result {
        var result: Result = deleteJson("$basePath/game.json")
        game.mechanics.forEach {
            result = result.and(deleteJson("$basePath/${it.name}"))
        }

        return result
    }

    fun saveJson(path: String, ele: JsonElement): Result
    fun loadJson(path: String): TimestampedJson
    fun deleteJson(path: String): Result
}