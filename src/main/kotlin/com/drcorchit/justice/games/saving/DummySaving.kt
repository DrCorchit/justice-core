package com.drcorchit.justice.games.saving

import com.drcorchit.justice.games.DummyGame
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonObject

class DummySaving(override val game: DummyGame): Saving {
    override fun save(): Result {
        return Result.succeed()
    }

    override fun load(info: JsonObject): Result {
        return Result.succeed()
    }

    override fun delete(): Result {
        return Result.succeed()
    }
}