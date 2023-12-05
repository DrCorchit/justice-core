package com.drcorchit.justice.game.io

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.lastModified
import com.drcorchit.justice.utils.json.toJson
import com.google.gson.JsonElement

abstract class AbstractIO(override var path: String, val game: Game) : IO {

    //Saving may be triggered after creation, after a state change, or after an api call.
    override fun save(): Result {
        var result: Result = saveJson("game.json", game.serialize())
        game.mechanics.forEach {
            result = result.and(saveJson("mechanics/${it.name}.json", it.serialize()))
        }

        return Result.succeed()
    }

    //Loads the game state from Json
    override fun load(): Result {
        val json = loadResource("game.json").toJson()
        val info = json.info.asJsonObject
        val metadataInfo = info.getAsJsonObject("metadata").deepCopy()
        val playersInfo = info.getAsJsonObject("players").deepCopy()
        val eventsInfo = info.getAsJsonObject("events").deepCopy()
        val mechanicsInfo = info.getAsJsonObject("mechanics").deepCopy()

        game.metadata.sync(metadataInfo)
        game.players.sync(playersInfo)
        game.events.sync(eventsInfo)
        game.mechanics.sync(mechanicsInfo, json.lastModified)

        return Result.succeed()
    }

    //Irreversibly deletes files or database entries associated with the game.
    override fun delete(): Result {
        var result: Result = deleteResource("game.json")
        game.mechanics.forEach {
            result = result.and(deleteResource("mechanics/${it.name}.json"))
        }
        return result
    }

    open fun extendBasePath(string: String): String {
        return "$path/$string"
    }

    protected fun saveJson(path: String, json: JsonElement): Result {
        return saveResource(path, json.prettyPrint().toByteArray())
    }
}