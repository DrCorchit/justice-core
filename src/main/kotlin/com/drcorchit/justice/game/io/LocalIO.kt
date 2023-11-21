package com.drcorchit.justice.game.io

import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.TimestampedJson
import com.google.gson.JsonElement
import java.io.File

class LocalIO(override val basePath: String) : IO {

    val parentFolder = File(basePath)

    init {
        require(parentFolder.isDirectory)
    }

    override fun saveJson(path: String, ele: JsonElement): Result {
        return IOUtils.overwriteFile(path, ele.prettyPrint())
    }

    override fun loadJson(path: String): TimestampedJson {
        return JsonUtils.parseFromFile(path)!!
    }

    override fun deleteJson(path: String): Result {
        return try {
            val file = File(path)
            if (file.exists()) {
                if (file.delete()) Result.succeed()
                else Result.failWithReason("Failed to delete file for an unknown reason.")
            } else {
                Result.failWithReason("File $path does not exist.")
            }
        } catch (e: Exception) {
            Result.failWithError(e)
        }
    }
}