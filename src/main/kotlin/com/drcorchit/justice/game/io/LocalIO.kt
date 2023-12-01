package com.drcorchit.justice.game.io

import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.TimestampedBytes
import com.google.gson.JsonElement
import java.io.File

class LocalIO(override val basePath: String) : IO {

    val parentFolder = File(basePath)

    init {
        parentFolder.mkdirs()
        require(parentFolder.isDirectory)
    }

    override fun save(path: String, ele: JsonElement): Result {
        return IOUtils.overwriteFile("$basePath/$path", ele.prettyPrint())
    }

    override fun load(path: String): TimestampedBytes {
        return File("$basePath/$path").let { it.readBytes() to it.lastModified() }
    }

    override fun delete(path: String): Result {
        val actualPath = "$basePath/$path"
        return try {
            val file = File(actualPath)
            if (file.exists()) {
                if (file.delete()) Result.succeed()
                else Result.failWithReason("Failed to delete file for an unknown reason.")
            } else {
                Result.failWithReason("File $actualPath does not exist.")
            }
        } catch (e: Exception) {
            Result.failWithError(e)
        }
    }
}