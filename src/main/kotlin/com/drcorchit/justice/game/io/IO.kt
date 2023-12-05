package com.drcorchit.justice.game.io

import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.TimestampedBytes

interface IO {
    //Location from which the game is saved and loaded.
    //May be an S3 or http url, or a file path.
    var path: String

    //Saving may be triggered after creation, after a state change, or after an api call.
    fun save(): Result

    //Loads the game state from Json
    fun load(): Result

    //Irreversibly deletes files or database entries associated with the game.
    fun delete(): Result

    //Low level functions. "Path" argument may be relative to the base path.
    fun saveResource(path: String, resource: ByteArray): Result
    fun loadResource(path: String): TimestampedBytes
    fun deleteResource(path: String): Result
}