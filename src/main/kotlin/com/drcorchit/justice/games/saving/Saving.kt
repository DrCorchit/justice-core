package com.drcorchit.justice.games.saving

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonObject

interface Saving {
    val game: Game

    //Saving may be triggered after creation, after a state change, or after an api call.
    fun save(): Result

    //Loads the game state from Json
    fun load(info: JsonObject): Result

    //Irreversibly deletes files or database entries associated with the game.
    fun delete(): Result
}