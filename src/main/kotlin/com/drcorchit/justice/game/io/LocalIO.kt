package com.drcorchit.justice.game.io

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.TimestampedBytes
import java.io.File

class LocalIO(path: String, game: Game) : AbstractIO(path, game) {

    override fun saveResource(path: String, resource: ByteArray): Result {
        val fullPath = extendBasePath(path)
        return IOUtils.overwriteFile(fullPath, resource)
    }

    override fun loadResource(path: String): TimestampedBytes {
        val fullPath = extendBasePath(path)
        return File(fullPath).let { it.readBytes() to it.lastModified() }
    }

    override fun deleteResource(path: String): Result {
        return try {
            val fullPath = extendBasePath(path)
            val file = File(fullPath)
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