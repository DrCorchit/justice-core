package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri

interface Types : HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("evaluating")

    val universe: TypeUniverse

    fun query(query: String): Result
    fun execute(command: String): Result
    fun getExecutionContext(allowSideEffects: Boolean, self: Thing<*>?): ExecutionContext
    fun getDryRunContext(allowSideEffects: Boolean, self: Type<*>?): DryRunContext
}