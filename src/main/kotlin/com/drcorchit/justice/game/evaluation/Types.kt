package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.types.source.TypeSource
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri

interface Types : HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("evaluating")

    //Contains global variables available in all evaluation contexts.
    val baseEnv: Environment
    val source: TypeSource

    fun query(query: String): Result
    fun execute(command: String): Result
}