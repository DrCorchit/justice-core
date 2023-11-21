package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri

interface Types: HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("evaluating")

    val baseEnv: Environment

    fun query(query: Expression): Result
    fun execute(command: Statement): Result

    fun getType(name: String): Evaluator<*>?
    fun getType(instance: Any): Evaluator<*>?
}